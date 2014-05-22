
package calendarBox;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

/**
 * Simple EDGE peer connecting via the NetPeerGroup.
 */
public class Edge_LAN {

    // Static

    public static final String Name_EDGE = "Edge Peer behind NAT for Calendar Box Service";
    public static final PeerID PID_EDGE = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name_EDGE.getBytes());
    public static final int TcpPort_EDGE = 9601;
    public static final int HttpPort_EDGE = 9701;
    public static final File ConfigurationFile_EDGE = new File("." + System.getProperty("file.separator") + Name_EDGE);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name_EDGE, ConfigurationFile_EDGE);

            System.out.println(PeerGroup.tcpProtoClassID);

            // Creation of the network manager
            final NetworkManager MyNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name_EDGE, ConfigurationFile_EDGE.toURI());

            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();

            // Setting Configuration
            MyNetworkConfigurator.setUseMulticast(true);

            MyNetworkConfigurator.setTcpPort(TcpPort_EDGE);

            if ( Tools.PopYesNoQuestion(Name_EDGE, "Do you want to enable TCP?") == JOptionPane.YES_OPTION ) {

                MyNetworkConfigurator.setTcpEnabled(true);
                MyNetworkConfigurator.setTcpIncoming(true);
                MyNetworkConfigurator.setTcpOutgoing(true);

            } else {

                MyNetworkConfigurator.setTcpEnabled(false);
                MyNetworkConfigurator.setTcpIncoming(false);
                MyNetworkConfigurator.setTcpOutgoing(false);

            }

            MyNetworkConfigurator.setHttpPort(HttpPort_EDGE);

            if ( Tools.PopYesNoQuestion(Name_EDGE, "Do you want to enable HTTP?") == JOptionPane.YES_OPTION ) {

                MyNetworkConfigurator.setHttpEnabled(true);
                MyNetworkConfigurator.setHttpIncoming(false);
                MyNetworkConfigurator.setHttpOutgoing(true);

            } else {

                MyNetworkConfigurator.setHttpEnabled(false);
                MyNetworkConfigurator.setHttpIncoming(false);
                MyNetworkConfigurator.setHttpOutgoing(false);

            }

            // Setting the Peer ID
            MyNetworkConfigurator.setPeerID(PID_EDGE);

            // Adding RDV seed
            MyNetworkConfigurator.clearRendezvousSeeds();

            String TheRdvSeed = "tcp://ece002.ece.cmu.edu:" + RendezVous.TcpPort;
            URI RendezVousSeedURI = URI.create(TheRdvSeed);
            MyNetworkConfigurator.addSeedRendezvous(RendezVousSeedURI);

            // Adding Relay seed
            MyNetworkConfigurator.clearRelaySeeds();

            String TheRelaySeed = "http://ece003.ece.cmu.edu:" + Relay.HttpPort_RELAY;
            URI RelaySeedURI = URI.create(TheRelaySeed);
            MyNetworkConfigurator.addSeedRelay(RelaySeedURI);

            String TheRelaySeed2 = "tcp://ece003.ece.cmu.edu:" + Relay.TcpPort_RELAY;
            URI RelaySeedURI2 = URI.create(TheRelaySeed2);
            MyNetworkConfigurator.addSeedRelay(RelaySeedURI2);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name_EDGE, "Start the JXTA network");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();

            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);
            
            // Stopping the network
            Tools.PopInformationMessage(Name_EDGE, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();

        } catch (IOException Ex) {

            System.err.println(Ex.toString());

        } catch (PeerGroupException Ex) {

            System.err.println(Ex.toString());

        }

    }

}
