package calendarBox;

import java.io.File;
import java.io.IOException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

public class RendezVous {
    // Static attributes
    public static final String Name = "RendezVous CalendarBox Service";
    public static final int TcpPort = 9600;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    // public static final 

    public static void main(String[] args) {
    	 try {
             
             // Check for removal of any existing configuration?
             Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
             
             // Creation of the network manager
             NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
                     Name, ConfigurationFile.toURI());
             
             // Retrieving the configurator
             NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
             
             // Setting configuration
             MyNetworkConfigurator.setTcpPort(TcpPort);
             MyNetworkConfigurator.setTcpEnabled(true);
             MyNetworkConfigurator.setTcpIncoming(true);
             MyNetworkConfigurator.setTcpOutgoing(true);
             Tools.CheckForMulticastUsage(Name, MyNetworkConfigurator);
             
             // Setting the Peer ID
             Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
             MyNetworkConfigurator.setPeerID(PID);

             // Starting the JXTA network
             Tools.PopInformationMessage(Name, "Start the JXTA network");
             PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();

             // Waiting for rendezvous connection
             Tools.PopInformationMessage(Name, "Waiting for other peers to connect");
             
             // Retrieving connected peers
             Tools.popConnectedPeers(NetPeerGroup.getRendezVousService(), Name);
             
             // Stopping the network
             Tools.PopInformationMessage(Name, "Stop the JXTA network");
             MyNetworkManager.stopNetwork();
             
         } catch (IOException Ex) {
             
             // Raised when access to local file and directories caused an error
             Tools.PopErrorMessage(Name, Ex.toString());
             
         } catch (PeerGroupException Ex) {
             
             // Raised when the net peer group could not be created
             Tools.PopErrorMessage(Name, Ex.toString());
             
         }

    }

}
