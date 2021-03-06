/*
 * Copyright (c) 2008 DawningStreams, Inc.  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *  
 *  3. The end-user documentation included with the redistribution, if any, must 
 *     include the following acknowledgment: "This product includes software 
 *     developed by DawningStreams, Inc." 
 *     Alternately, this acknowledgment may appear in the software itself, if 
 *     and wherever such third-party acknowledgments normally appear.
 *  
 *  4. The name "DawningStreams,Inc." must not be used to endorse or promote
 *     products derived from this software without prior written permission.
 *     For written permission, please contact DawningStreams,Inc. at 
 *     http://www.dawningstreams.com.
 *  
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *  DAWNINGSTREAMS, INC OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  DawningStreams is a registered trademark of DawningStreams, Inc. in the United 
 *  States and other countries.
 *  
 */

package calendarBox;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

public class Edge {
    
    public static final String Name = "Edge calendar box";
    public static final int TcpPort_EDGE = 9601;
    public static final int HttpPort_EDGE = 9701;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
            
            // Checking if RendezVous_Jack should be a seed
            MyNetworkConfigurator.clearRendezvousSeeds();
            // String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVous.TcpPort;
            String TheSeed = "tcp://ece002.ece.cmu.edu:" + RendezVous.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);

            MyNetworkConfigurator.setTcpPort(TcpPort_EDGE);

            if ( Tools.PopYesNoQuestion(Name, "Do you want to enable TCP?") == JOptionPane.YES_OPTION ) {

                MyNetworkConfigurator.setTcpEnabled(true);
                MyNetworkConfigurator.setTcpIncoming(true);
                MyNetworkConfigurator.setTcpOutgoing(true);

            } else {

                MyNetworkConfigurator.setTcpEnabled(false);
                MyNetworkConfigurator.setTcpIncoming(false);
                MyNetworkConfigurator.setTcpOutgoing(false);

            }

            MyNetworkConfigurator.setHttpPort(HttpPort_EDGE);

            if ( Tools.PopYesNoQuestion(Name, "Do you want to enable HTTP?") == JOptionPane.YES_OPTION ) {

                MyNetworkConfigurator.setHttpEnabled(true);
                MyNetworkConfigurator.setHttpIncoming(false);
                MyNetworkConfigurator.setHttpOutgoing(true);

            } else {

                MyNetworkConfigurator.setHttpEnabled(false);
                MyNetworkConfigurator.setHttpIncoming(false);
                MyNetworkConfigurator.setHttpOutgoing(false);

            }

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            MyNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous\nconnection with "
                    + RendezVous.Name + " for maximum 2 minutes");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();
            
            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);
            
            if (MyNetworkManager.waitForRendezvousConnection(1200)) {
                
                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(),Name);
                
            } else {
                
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");

            }
            
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