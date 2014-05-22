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
import java.net.URI;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import net.jxta.access.AccessService;
import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.document.Advertisement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.id.ID;
import net.jxta.impl.membership.pse.DialogAuthenticator;
import net.jxta.membership.InteractiveAuthenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.rendezvous.RendezVousService;

public class Tools {

    public Tools() {

    }
    
    public static void popConnectedRendezvous(RendezVousService TheRendezVous, String Name) {
        
        Enumeration<ID> TheList = TheRendezVous.getConnectedRendezVous();
        int Count = 0;
        
        while (TheList.hasMoreElements()) {
            
            Count = Count + 1;

            PopInformationMessage(Name, "Connected to rendezvous:\n\n"
                    + TheList.nextElement().toString());
            
        }
        
        if (Count==0) {
            
            PopInformationMessage(Name, "No rendezvous connected to this rendezvous!");
            
        }

    }
    
    public static void popConnectedPeers(RendezVousService TheRendezVous, String Name) {
        
        Enumeration<ID> TheList = TheRendezVous.getConnectedPeers();
        int Count = 0;
        
        while (TheList.hasMoreElements()) {
            
            Count = Count + 1;
            
            PopInformationMessage(Name, "Peer connected to this rendezvous:\n\n"
                    + TheList.nextElement().toString());
            
        }
        
        if (Count==0) {
            
            PopInformationMessage(Name, "No peers connected to this rendezvous!");
            
        }
        
    }
    
    public static void CheckForMulticastUsage(String Name, NetworkConfigurator TheNC) throws IOException {
        
        if (JOptionPane.YES_OPTION==PopYesNoQuestion(Name, "Do you want to enable multicasting?")) {

            TheNC.setUseMulticast(true);
            
        } else {
            
            TheNC.setUseMulticast(false);
            
        }
        
    }
    
    public static void CheckForRendezVousSeedAddition(String Name, String TheSeed, NetworkConfigurator TheNC) {
        
        Tools.PopInformationMessage(Name, "Connecting to Rendezvous...");

        URI LocalSeedingRendezVousURI = URI.create(TheSeed);
        TheNC.addRdvSeedingURI(LocalSeedingRendezVousURI);
        


    };
    
    public static void PopInformationMessage(String Name, String Message) {
        
        JOptionPane.showMessageDialog(null, Message, Name, JOptionPane.INFORMATION_MESSAGE);
        
    }
    
    public static void PopErrorMessage(String Name,String Message) {
        
        JOptionPane.showMessageDialog(null, Message, Name, JOptionPane.ERROR_MESSAGE);
        
    }
    
    public static void PopWarningMessage(String Name, String Message) {
        
        JOptionPane.showMessageDialog(null, Message, Name, JOptionPane.WARNING_MESSAGE);
        
    }
    
    public static int PopYesNoQuestion(String Name, String Question) {
        
        return JOptionPane.showConfirmDialog(null, Question, Name, JOptionPane.YES_NO_OPTION);
        
    }
    
    public static String PopInputMessage(String Name, String Question)
    {
    	return JOptionPane.showInputDialog(null, Question, Name, JOptionPane.PLAIN_MESSAGE);
    }
    
    public static void DeleteConfigurationInDefaultHome() {
        
        String DefaultHome = "." + File.separator + ".jxta";
        Tools.RecursiveDelete(new File(DefaultHome));
        
    }
    
    public static void CheckForExistingConfigurationDeletion(String Name, File ConfigurationFile) throws IOException {
        
        if (JOptionPane.YES_OPTION==PopYesNoQuestion(Name, "Do you want to delete the existing configuration in:\n\n"
                + ConfigurationFile.getCanonicalPath())) {

            Tools.RecursiveDelete(ConfigurationFile);
            
        }
        
    }
    
    public static void RecursiveDelete(File TheFile) {
        
        File[] SubFiles = TheFile.listFiles();
        
        if (SubFiles!=null) {
        
            for(int i=0;i<SubFiles.length;i++) {

                if (SubFiles[i].isDirectory()) {
                
                    RecursiveDelete(SubFiles[i]);
                
                }
            
                SubFiles[i].delete();
            
            }
            
        TheFile.delete();

        }
        
    }
    
    public static void DisplayMessageContent(String Name, Message TheMessage) {
        
        try {
            
            String ToDisplay = "--- Message Start ---\n";

            ElementIterator MyElementIterator = TheMessage.getMessageElements();
            
            while (MyElementIterator.hasNext()) {
                
                MessageElement MyMessageElement = MyElementIterator.next();
                
                ToDisplay = ToDisplay + "Element : " +
                        MyElementIterator.getNamespace() + " :: "
                        + MyMessageElement.getElementName() 
                        + "  [" + MyMessageElement + "]\n";
                
            }
            
            ToDisplay = ToDisplay + "--- Message End ---";
            
            PopInformationMessage(Name,ToDisplay);
            
        } catch (Exception Ex) {
            
            PopErrorMessage(Name, Ex.toString());
            
        }
        
    }
    
    public static final void GoToSleep(long Duration) {
        
        long Delay = System.currentTimeMillis() + Duration;

        while (System.currentTimeMillis()<Delay) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException Ex) {
                // We don't care
            }
        }
        
    }
    
    public static void RunMembershipServiceExample() {
        
        String Name = "Membership Service Example";
        
        try {

            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name);
            

            // Starting JXTA
            Tools.PopInformationMessage(Name, "Starting JXTA network");
            PeerGroup TheNetPeerGroup = MyNetworkManager.startNetwork();

            // Retrieving the membership service
            MembershipService DefaultMembershipService = TheNetPeerGroup.getMembershipService();
            
            Advertisement TheAdvertisement = DefaultMembershipService.getImplAdvertisement();
            
            System.out.println(TheAdvertisement);
            
            // Proper way to create credentials
            AuthenticationCredential MyAuthenticationCredential =  new AuthenticationCredential(
                    TheNetPeerGroup, "InteractiveAuthentication", null);

            // Applying for membership
            InteractiveAuthenticator MyAuthenticator = (DialogAuthenticator)
                DefaultMembershipService.apply(MyAuthenticationCredential);

            MyAuthenticator.interact();
            
            if (!MyAuthenticator.isReadyForJoin()) {
                
                System.out.println("Authentication failed");
                
            } else {
                
                System.out.println("Authentication OK");
                
            }

            // Retrieving credentials, since our membership has been accepted
            Credential MyCredential = DefaultMembershipService.join(MyAuthenticator);
            
            // Stopping JXTA
            Tools.PopInformationMessage(Name, "Stopping JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (ProtocolNotSupportedException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());

        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

    public static void RunAccessServiceExample() {
        
        String Name = "Access Service Example";
        
        try {

            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name);

            // Starting JXTA
            PeerGroup TheNetPeerGroup = MyNetworkManager.startNetwork();

            // Retrieving the access service
            AccessService MyAccessService = TheNetPeerGroup.getAccessService();
            
            System.out.println("Access Service class: " + MyAccessService.getClass().getName());
            
            // Stopping JXTA
            MyNetworkManager.stopNetwork();
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }
    
    public static void main(String[] args) {
        
        // Running Code examples
        RunAccessServiceExample();
        // RunMembershipServiceExample();
        
    }

}
