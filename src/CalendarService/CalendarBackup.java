package CalendarService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JOptionPane;

import GUI.Event;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaMulticastSocket;
import calendarBox.Communication;
import calendarBox.Relay;
import calendarBox.RendezVous;
import calendarBox.Tools;

public class CalendarBackup {
		//static
	    public static final String Name = "BackupPeer";
	    public static final int TcpPort_BACKUP = 9750 + new Random().nextInt(100);
	    public static final int HttpPort_BACKUP = 9800 + new Random().nextInt(100);
	    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
	    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + "CalendarBox");
	    public static NetworkManager MyNetworkManager = null;
	    public static final String[] rendezvousList = {"ece002.ece.cmu.edu", "ece003.ece.cmu.edu"};
	    //non-static
	    //public ArrayList<Event> localEvent = new ArrayList<Event>();
		//public ArrayList<Event> inEvent = new ArrayList<Event>();
		private int eventNum = 0;


		public Communication comm = null;
		
		
		public CalendarBackup() {
            // Removing any existing configuration?
            try {
				Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            // Creation of the network manager
            
			try {
				MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
				        Name, ConfigurationFile.toURI());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            this.comm = new Communication(TcpPort_BACKUP, MyNetworkManager, false);
			try {
				this.comm.start();
			} catch (PeerGroupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.comm.fetch_advertisements();
		}

		public void configBackUp()
		{
			try {
	            
	            // Retrieving the network configurator
	            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
	            
	            // Checking if RendezVous_Jack should be a seed
	            MyNetworkConfigurator.clearRendezvousSeeds();
	            
	            String seedIP = this.rendezvousList[0];
	            String TheSeed = "tcp://" + seedIP + ":" + RendezVousTest.TcpPort;
	            // String TheSeed = "tcp://ece002.ece.cmu.edu:" + RendezVous.TcpPort;
	            // String TheSeed = "tcp://printart2.isr.ist.utl.pt:" + RendezVousTest.TcpPort;
	            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);
	            MyNetworkConfigurator.setTcpPort(CalendarBackup.TcpPort_BACKUP);
	            
	            if (InetAddress.getLocalHost().getHostAddress().startsWith("192") || InetAddress.getLocalHost().getHostAddress().startsWith("127"))
	            {
		            String relayIP = Tools.PopInputMessage("Relay Input", "You are behind NAT.\n"+
		            							"Please Enter the domain name for Relay Node:");
		            MyNetworkConfigurator.clearRelaySeeds();

		            String TheRelaySeed = "tcp://" + relayIP + ":" + RelayTest.TcpPort_RELAY;
		            URI RelaySeedURI = URI.create(TheRelaySeed);
		            MyNetworkConfigurator.addSeedRelay(RelaySeedURI);
	            }

                MyNetworkConfigurator.setTcpEnabled(true);
                MyNetworkConfigurator.setTcpIncoming(true);
                MyNetworkConfigurator.setTcpOutgoing(true);



	            // Setting the Peer ID
	            //Tools.PopInformationMessage(CalendarBackup.Name, "Setting the peer ID to :\n\n" + CalendarBackup.PID.toString());
	            MyNetworkConfigurator.setPeerID(CalendarBackup.PID);

	            // Starting the JXTA network
	            Tools.PopInformationMessage(CalendarBackup.Name, "Start the JXTA network and to wait for a rendezvous\nconnection with "
	                    + RendezVous.Name + " for maximum 1 minutes");
	            PeerGroup NetPeerGroup = CalendarBackup.MyNetworkManager.startNetwork();
	            
	            // Disabling any rendezvous autostart
	            NetPeerGroup.getRendezVousService().setAutoStart(false);
	            
	            int i = 0;
	            while(i < this.rendezvousList.length) {
	            	if (CalendarBackup.MyNetworkManager.waitForRendezvousConnection(60000)) {
	                
	            		Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(),this.Name);
	            		break;
	                
	            	} else {
	            		
	            		if(i == this.rendezvousList.length - 1) {
	            			Tools.PopInformationMessage("", "No rendezvous found.Exit.");
	            			System.exit(0);
	            		}
	            		seedIP = this.rendezvousList[++ i];
	    	            TheSeed = "tcp://" + seedIP + ":" + RendezVousTest.TcpPort;
	    	            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);
	            	}
	            	
	            }
	            Tools.PopInformationMessage("Message", "The backup is running");
	        } catch (IOException Ex) {
	            
	            // Raised when access to local file and directories caused an error
	            Tools.PopErrorMessage(CalendarBackup.Name, Ex.toString());
	            
	        } catch (PeerGroupException Ex) {
	            
	            // Raised when the net peer group could not be created
	            Tools.PopErrorMessage(CalendarBackup.Name, Ex.toString());
	            
	        }
		}
		

		
		
		
		public void sendOutNewEvent(Event newEvent, String kind) throws IOException {
			
			ArrayList<PeerID> list = new ArrayList<PeerID>();
			for(String part : newEvent.getParticipants()) {
				System.out.println("part is" + part + "Peer_name is" + this.comm.getPeer_name());
				if(!part.equals(this.comm.getPeer_name())) {
					PeerID tmpID = this.comm.getMap().get(part);
					if(tmpID != null) {
						list.add(tmpID);
					}
					else {
						
						System.out.println("cannot find this participant" + part);
						System.out.println("The map is:");
						for(String s : this.comm.getMap().keySet())
							System.out.println("key:" + s + "  value:" + this.comm.getMap().get(s));
					}
				}
			}
			if(kind.equals("create")) {
				if(list.size() != 0) {

					for(PeerID p : list) {

						this.comm.send_to_peer(new CalendarMessage(Enumeration.CREATE, newEvent), p);
					}
				}
			}
			else if(kind.equals("modify")) {
				if(!newEvent.getOrganizer().equals(this.comm.getPeer_name())) {
					list.add(this.comm.getMap().get(newEvent.getOrganizer()));
				}
				else {
					System.out.println("Event's organizer is offline");
				}

				for(PeerID p : list) {
					
					this.comm.send_to_peer(new CalendarMessage(Enumeration.MODIFY, newEvent), p);
				}
			}
			else if(kind.equals("delete")) {
				if(!newEvent.getOrganizer().equals(this.comm.getPeer_name())) {
					if(this.comm.getMap().containsKey(newEvent.getOrganizer())) {
						list.add(this.comm.getMap().get(newEvent.getOrganizer()));
					}
					else {
						System.out.println("Event's organizer is offline");
					}
				}

				for(PeerID p : list) {
					this.comm.send_to_peer(new CalendarMessage(Enumeration.DELETE, newEvent), p);
				}
				
			}
		}


	    public int getEventNum() {
			return eventNum;
		}


		public void setEventNum(int eventNum) {
			this.eventNum = eventNum;
		}
		
		public void increaseEventNum() {
			this.eventNum = this.eventNum + 1;
		}
		
		public static void main(String[] args) {
	        
			CalendarBackup instance = new CalendarBackup();
	        instance.configBackUp();
	            	            
	        // Stopping the network
	        Tools.PopInformationMessage(Name, "Stop the JXTA network");
	        MyNetworkManager.stopNetwork();

	    }
}
