
package CalendarService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import calendarBox.Communication;
import calendarBox.Hello;
import calendarBox.RendezVous;
import calendarBox.Tools;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

/**
 * Simple RELAY peer.
 */
public class RelayTest {

    // Static

    public static final String Name_RELAY = "CalendarBox Relay";
    public static final PeerID PID_RELAY = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name_RELAY.getBytes());
    public static final int HttpPort_RELAY = 9702;
    public static final int TcpPort_RELAY = 9701;
    public static final File ConfigurationFile_RELAY = new File("." + System.getProperty("file.separator") + "Relay");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            // Removing any existing configuration?
           Tools.CheckForExistingConfigurationDeletion(Name_RELAY, ConfigurationFile_RELAY);

            // Creation of the network manager
            final NetworkManager MyNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.RELAY,
                    Name_RELAY, ConfigurationFile_RELAY.toURI());

            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();

            // Setting Configuration
            MyNetworkConfigurator.setUseMulticast(true);

            MyNetworkConfigurator.setTcpPort(TcpPort_RELAY);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);

            MyNetworkConfigurator.setHttpPort(HttpPort_RELAY);
            MyNetworkConfigurator.setHttpEnabled(true);
            MyNetworkConfigurator.setHttpIncoming(true);
            MyNetworkConfigurator.setHttpOutgoing(true);

            // Setting the Peer ID
            MyNetworkConfigurator.setPeerID(PID_RELAY);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name_RELAY, "Start the JXTA network");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();
            
            //Test for Hello class
            Communication hello = new Communication(TcpPort_RELAY, MyNetworkManager, false);
            hello.start(); 
            hello.fetch_advertisements();
            
            // Stopping the network
            Tools.PopInformationMessage(Name_RELAY, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();

        } catch (IOException Ex) {

            System.err.println(Ex.toString());

        } catch (PeerGroupException Ex) {

            System.err.println(Ex.toString());

        }

    }

}
