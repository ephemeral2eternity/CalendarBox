package calendarBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import CalendarService.CalendarBackup;
import CalendarService.CalendarMessage;
import CalendarService.Enumeration;
import GUI.Event;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

public class Communication implements DiscoveryListener, PipeMsgListener {

    // When developing you should handle these exceptions, I don't to lessen the clutter of start()
    public static void main(String[] args) throws PeerGroupException, IOException {

        // JXTA logs a lot, you can configure it setting level here
        Logger.getLogger("net.jxta").setLevel(Level.ALL);


        // Randomize a port to use with a number over 1000 (for non root on unix)
        // JXTA uses TCP for incoming connections which will conflict if more than
        // one Hello runs at the same time on one computer.
        int port = 9000 + new Random().nextInt(100);

        Communication hello = new Communication(port, null, false);
        hello.start(); 
        hello.fetch_advertisements();
    }


	private String peer_name;



	private PeerID peer_id;
    private File conf;
    private NetworkManager manager;
    private HashMap<String, PeerID> peerMap = new HashMap<String, PeerID>();//populating of this is to-do



	private String backUpName = null;
    private PeerID backUpID = null;
    //private boolean isRendezvous;


	public ArrayList<Event> localEvent = new ArrayList<Event>();
	public ArrayList<Event> inEvent = new ArrayList<Event>();
	
    public Communication(int port, NetworkManager manager, boolean isRendezvous) {
        // Add a random number to make it easier to identify by name, will also make sure the ID is unique 
        // peer_name = "Peer " + new Random().nextInt(1000000); 
    	
    	peer_name = manager.getInstanceName();

        // This is what you will be looking for in Wireshark instead of an IP, hint: filter by "jxta"
        // peer_id = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, peer_name.getBytes());
    	peer_id = manager.getPeerID();

        // Here the local peer cache will be saved, if you have multiple peers this must be unique
        conf = new File("." + System.getProperty("file.separator") + peer_name);

        this.manager = manager;
        //this.isRendezvous = isRendezvous;

        NetworkConfigurator configurator;
        try {
            // Settings Configuration
            configurator = manager.getConfigurator();
            configurator.setTcpPort(port);
            configurator.setTcpEnabled(true);
            configurator.setTcpIncoming(true);
            configurator.setTcpOutgoing(true);
            configurator.setUseMulticast(true);
            configurator.setPeerID(peer_id);
        } 
        catch (IOException e) {
            // Never caught this one but let me know if you do =)
            e.printStackTrace();
        }
    }

    private static final String subgroup_name = "CalendarBox Group";
    private static final String subgroup_desc = "Share Calendar Events with Group Members";
    private static final PeerGroupID subgroup_id = IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, subgroup_name.getBytes());

    private static final String unicast_name = "CalendarBox Unicast";
    private static final String multicast_name = "CalendarBox Multicast";

    private static final String service_name = "CalendarBox Services";

    private PeerGroup subgroup;
    private PipeService pipe_service;
    private PipeID unicast_id;
    private PipeID multicast_id;
    private PipeID service_id;
    private DiscoveryService discovery;
    private ModuleSpecAdvertisement mdadv;

    
    public HashMap<String, PeerID> getMap() {
		return peerMap;
	}

	public void setMap(HashMap<String, PeerID> map) {
		this.peerMap = map;
	}
	
    public String getPeer_name() {
		return peer_name;
	}

	public void setPeer_name(String peer_name) {
		this.peer_name = peer_name;
	}
	
    public PeerID getPeer_id() {
		return peer_id;
	}

	public void setPeer_id(PeerID peer_id) {
		this.peer_id = peer_id;
	}
	
    public void start() throws PeerGroupException, IOException {
        // Launch the missiles, if you have logging on and see no exceptions
        // after this is ran, then you probably have at least the jars setup correctly.
        PeerGroup net_group = manager.startNetwork();

        // Connect to our subgroup (all groups are subgroups of Netgroup)
        // If the group does not exist, it will be automatically created
        // Note this is suggested deprecated, not sure what the better way is
        ModuleImplAdvertisement mAdv = null;
        try {
            mAdv = net_group.getAllPurposePeerGroupImplAdvertisement();
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
        subgroup = net_group.newGroup(subgroup_id, mAdv, subgroup_name, subgroup_desc);

        // A simple check to see if connecting to the group worked
        if (Module.START_OK != subgroup.startApp(new String[0]))
            System.err.println("Cannot start child peergroup");

        // We will spice things up to a more interesting level by sending unicast and multicast messages
        // In order to be able to do that we will create to listeners that will listen for
        // unicast and multicast advertisements respectively. All messages will be handled by Hello in the
        // pipeMsgEvent method. 

        unicast_id = IDFactory.newPipeID(subgroup.getPeerGroupID(), unicast_name.getBytes());
        multicast_id = IDFactory.newPipeID(subgroup.getPeerGroupID(), multicast_name.getBytes());

        pipe_service = subgroup.getPipeService();
        pipe_service.createInputPipe(get_advertisement(unicast_id, false), this);
        pipe_service.createInputPipe(get_advertisement(multicast_id, true), this);

        // In order to for other peers to find this one (and say hello) we will
        // advertise a Hello Service.
        discovery = subgroup.getDiscoveryService();
        discovery.addDiscoveryListener(this);        

        ModuleClassAdvertisement mcadv = (ModuleClassAdvertisement)
        AdvertisementFactory.newAdvertisement(ModuleClassAdvertisement.getAdvertisementType());

        mcadv.setName("STACK-OVERFLOW:HELLO");
        mcadv.setDescription("Tutorial example to use JXTA module advertisement Framework");

        ModuleClassID mcID = IDFactory.newModuleClassID();

        mcadv.setModuleClassID(mcID);

        // Let the group know of this service "module" / collection
        discovery.publish(mcadv);
        discovery.remotePublish(mcadv);

        mdadv = (ModuleSpecAdvertisement)
                AdvertisementFactory.newAdvertisement(ModuleSpecAdvertisement.getAdvertisementType());
        mdadv.setName("STACK-OVERFLOW:HELLO");
        mdadv.setVersion("Version 1.0");
        mdadv.setCreator("sun.com");
        mdadv.setModuleSpecID(IDFactory.newModuleSpecID(mcID));
        mdadv.setSpecURI("http://www.jxta.org/Ex1");

        service_id = IDFactory.newPipeID(subgroup.getPeerGroupID(), service_name.getBytes());
        PipeAdvertisement pipeadv = get_advertisement(service_id, false);
        mdadv.setPipeAdvertisement(pipeadv);

        // Let the group know of the service
        discovery.publish(mdadv);
        discovery.remotePublish(mdadv);

        // Start listening for discovery events, received by the discoveryEvent method
        pipe_service.createInputPipe(pipeadv, this);
    }

    private static PipeAdvertisement get_advertisement(PipeID id, boolean is_multicast) {
        PipeAdvertisement adv = (PipeAdvertisement )AdvertisementFactory.
            newAdvertisement(PipeAdvertisement.getAdvertisementType());
        adv.setPipeID(id);
        if (is_multicast)
            adv.setType(PipeService.PropagateType); 
        else 
            adv.setType(PipeService.UnicastType); 
        adv.setName("This however");
        adv.setDescription("does not really matter");
        return adv;
    }

    @Override 
    public void discoveryEvent(DiscoveryEvent event) {
        // Found another peer! Let's say hello shall we!
        // Reformatting to create a real peer id string
        String found_peer_id = "urn:jxta:" + event.getSource().toString().substring(7);
        IDNamePair pair = new IDNamePair(this.peer_name, this.peer_id);
        
        CalendarMessage pairMsg = null;
        //if(!this.isRendezvous) {
            pairMsg = new CalendarMessage(Enumeration.NOTIFY, pair);
            try {
            	send_to_peer(pairMsg, (PeerID)IDFactory.fromURI(new URI(found_peer_id)));
            } catch (URISyntaxException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            } 
        //}
    }


    public void send_to_peer(CalendarMessage message, PeerID found_peer_id) {
        // This is where having the same ID is important or else we wont be
        // able to open a pipe and send messages
        PipeAdvertisement adv = get_advertisement(unicast_id, false);

        // Send message to all peers in "ps", just one in our case
        Set<PeerID> ps = new HashSet<PeerID>();
        //ps.add((PeerID)IDFactory.fromURI(new URI(found_peer_id)));

		ps.add(found_peer_id);


        // A pipe we can use to send messages with
        OutputPipe sender = null;
        try {
            sender = pipe_service.createOutputPipe(adv, ps, 1000);
        } 
        catch (IOException e) {
            // Thrown if there was an error opening the connection, check firewall settings
            //e.printStackTrace();
        }

        Message msg = new Message();
        MessageElement fromElem = null;
        MessageElement msgElem = null;
        MessageElement nameElem = null;
        fromElem = new ByteArrayMessageElement("From", null, peer_id.toString().getBytes(), null);
        
        ByteArrayOutputStream bout= new ByteArrayOutputStream();
        ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(bout);
	        oout.writeObject(message); 
	        oout.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        byte[] sendBuff=bout.toByteArray(); 
        
		msgElem = new ByteArrayMessageElement("Msg", null, sendBuff, null);
		nameElem = new ByteArrayMessageElement("Name", null, peer_name.getBytes(), null);


        msg.addMessageElement(fromElem);
        msg.addMessageElement(msgElem);
        msg.addMessageElement(nameElem);


        try {
            sender.send(msg);
            oout.close();
            bout.close();
        } catch (IOException e) {
  	
            // Check, firewall, settings.
            try {
				oout.close();
	            bout.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

           // e.printStackTrace();
            
        }
        catch (NullPointerException e1) {
        	
        }
        
    }

    @Override 
    public void pipeMsgEvent(PipeMsgEvent event) {
        // Someone is sending us a message!
        try {
            Message msg = event.getMessage();
            byte[] msgBytes = msg.getMessageElement("Msg").getBytes(true);  
            byte[] fromBytes = msg.getMessageElement("From").getBytes(true); 
            byte[] nameBytes = msg.getMessageElement("Name").getBytes(true);
            
            String from = new String(fromBytes);
            
            
            ByteArrayInputStream bout= new ByteArrayInputStream(msgBytes);
            ObjectInputStream oout=new ObjectInputStream(bout);
            CalendarMessage rcvMsg = (CalendarMessage)oout.readObject();
            
            //String message = new String(msgBytes);
            String peerNameStr = new String(nameBytes);
            
            System.out.println(rcvMsg.toString() + " says : it is from :" + from + " with peer name: " + peerNameStr);
            processRcvMsg(peerNameStr, rcvMsg, from);
        }
        catch (Exception e) {
            // You will notice that JXTA is not very specific with exceptions...
            e.printStackTrace();
        }
    }
    
    public void processRcvMsg(String peerNameStr, CalendarMessage msg, String from) {
    	Enumeration e = msg.getKind();
    	

    	if(e.equals(Enumeration.CREATE)) {
    		this.inEvent.add((Event)msg.getData());
    		Event evt =  (Event)msg.getData();
    		String y = "/tmp/"+ evt.getFname();  // this fname will be from e.getFname(); 
	        Path file2 = Paths.get(y);
	        try {
	               // Create the empty file with default permissions, etc.
               Files.createFile(file2);
               Files.write(file2, evt.getFileData());
           }catch (FileAlreadyExistsException x){}
           catch (IOException x) {
               // Some other sort of failure, such as permissions.
               System.err.format("createFile error: %s%n", x);
           }
    	}
    	else if(e.equals(Enumeration.DELETE)) {
    		this.inEvent.remove((Event)msg.getData());
    	}
    	else if(e.equals(Enumeration.MODIFY)) {

    		Event newEvent = (Event)msg.getData();
    		String organizer = newEvent.getOrganizer();
    		int seqNum = newEvent.getSeqNum();
    		for(Event event : this.inEvent) {
    			if(event.getOrganizer().equals(organizer) && event.getSeqNum() == seqNum) {
    				this.inEvent.remove(event);
    				this.inEvent.add(newEvent);
    				break;
    			}
    		}
    	}
    	else if(e.equals(Enumeration.NOTIFY)) {
    		IDNamePair pair = (IDNamePair)msg.getData();
    		if(!pair.getName().equals(RendezVous.Name)) {
    			if(pair.getName().equals(CalendarBackup.Name)) {
    				this.backUpName = pair.getName();
    				this.backUpID = pair.getId();
    			}
    			else {
    				this.peerMap.put(pair.getName(), pair.getId());
    			}
    		}
    		
    		//send back its own name and id
    		IDNamePair sendBackPair = new IDNamePair(this.peer_name, this.peer_id);
            CalendarMessage pairMsg = new CalendarMessage(Enumeration.ACK, sendBackPair);

    		send_to_peer(pairMsg, pair.getId());

    	}
    	else if(e.equals(Enumeration.ACK)) {
    		IDNamePair pair = (IDNamePair)msg.getData();
    		if(!pair.getName().equals(RendezVous.Name)) {
    			if(pair.getName().equals(CalendarBackup.Name)) {
    				this.backUpName = pair.getName();
    				this.backUpID = pair.getId();
    			}
    			else {
    				this.peerMap.put(pair.getName(), pair.getId());
    			}
    		}
    	}
    	else if(e.equals(Enumeration.CHECKUPDATE)) {
    		ArrayList<NameSeqPair> pairList = (ArrayList<NameSeqPair>)msg.getData();		
    		ArrayList<Event> missingEvents = getMissingEvents(pairList, peerNameStr);
    		
    		if(this.peerMap.containsKey(peerNameStr)) {
    			PeerID respondID = this.peerMap.get(peerNameStr);

				send_to_peer(new CalendarMessage(Enumeration.REUPDTAE, missingEvents), respondID);
    			
    		}
    		else {
    			Tools.PopInformationMessage("Warning", "Cannot find this user in network!");
    		}
    	}
    	else if(e.equals(Enumeration.REUPDTAE)) {
    		ArrayList<Event> missingEvts = (ArrayList<Event>)msg.getData();
			for(Event evt : missingEvts) {
				evt.setOK(true);
				if(evt.getOrganizer().equals(this.peer_name)) {
					this.localEvent.add(evt);
				}
				this.inEvent.add(evt);
			}
    	}
    	else if(e.equals(Enumeration.GETFILE)) {
    		ArrayList<Event> involveEvents = new ArrayList<Event>();
    		for(Event event : this.inEvent) {

    			if(event.getOrganizer().equals(peerNameStr) || event.getParticipants().contains(peerNameStr)) {
    				involveEvents.add(event);
    			}
    		}
    		if(this.peerMap.containsKey(peerNameStr)) {

				send_to_peer(new CalendarMessage(Enumeration.GETRESPOND, involveEvents), this.peerMap.get(peerNameStr));
    		}
    		else {
    			Tools.PopInformationMessage("Warning", "Cannot find this user!!");
    		}
    	}
    	else if(e.equals(Enumeration.GETRESPOND)) {
    		ArrayList<Event> rcvEvents = (ArrayList<Event>)msg.getData();
    		for(Event event : rcvEvents) {
    			event.setOK(true);
    			if(event.getOrganizer().equals(this.peer_name) && !this.localEvent.contains(event)) {
    				this.localEvent.add(event);
    			}
    			if(!this.inEvent.contains(event)) {
    				this.inEvent.add(event);
    			}
    		}
    	}
    	else if(e.equals(Enumeration.ASKFOROK)) {
    		Event evt = (Event)msg.getData();
    		for(Event event : this.inEvent) {
    			if(event.getOrganizer().equals(evt.getOrganizer()) && event.getSeqNum() == evt.getSeqNum()) {
    				send_to_peer(new CalendarMessage(Enumeration.RESPONDOK, event), this.getMap().get(peerNameStr));
    				break;
    			}
    		}
    	}
    	else if(e.equals(Enumeration.RESPONDOK)) {
    		Event evt = (Event)msg.getData();
    		if(!evt.isOK()) {
    			for(Event event : this.inEvent) {
    				if(event.getOrganizer().equals(evt.getOrganizer()) && event.getSeqNum() == evt.getSeqNum()) {
    					event.setOK(false);
    					break;
    				}
    			}
    		}
    	}
    	else {
    		System.out.println("Invalid Message");
    	}
    	
    }
    /**
     * We will not find anyone if we are not regularly looking
     */
    public void fetch_advertisements() {
      new Thread("fetch advertisements thread") {
         public void run() {
            while(true) {
                discovery.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", "STACK-OVERFLOW:HELLO", 1, null);
                try {
                    sleep(10000);

                }
                catch(InterruptedException e) {} 
            }
         }
      }.start();
   }


	/**
	 * Get the missing events
	 */
	public ArrayList<Event> getMissingEvents(ArrayList<NameSeqPair> pairList, String peerName) {
		
		ArrayList<Event> missingEvent = new ArrayList<Event>();
		
		for(Event curEvt: this.inEvent) {
			
			String curOrganizer = curEvt.getOrganizer();
			int curSeqNum = curEvt.getSeqNum();
			ArrayList<String> curParticipants = curEvt.getParticipants();	
			// peerName is not involved in current event
			if(!isInEvent(peerName, curOrganizer, curParticipants)) 
				continue;
			// See whether this is a missing event
			if(isMissingEvent(pairList, curOrganizer, curSeqNum))
				missingEvent.add(curEvt);
		}
		return missingEvent;
	}
	/**
	 * See whether a peer participates in an event
	 */
	private boolean isInEvent(String peerName, String organizer, ArrayList<String> participants) {
		
		if(peerName.equals(organizer))
			return true;
		for(String curPeer: participants) {
			if(peerName.equals(curPeer)) 
				return true;
		}
		return false;
	}
	
	/**
	 * See whether this is a missing event
	 */
	private boolean isMissingEvent(ArrayList<NameSeqPair> pairList, String organizer, int seqNum) {
		for(NameSeqPair curPair: pairList) {
			if(curPair.name.equals(organizer) && curPair.seqNum == seqNum)
				return false;
		}
		return true;
	}
	
    public String getBackUpName() {
		return backUpName;
	}

	public void setBackUpName(String backUpName) {
		this.backUpName = backUpName;
	}

	public PeerID getBackUpID() {
		return backUpID;
	}

	public void setBackUpID(PeerID backUpID) {
		this.backUpID = backUpID;
	}
}