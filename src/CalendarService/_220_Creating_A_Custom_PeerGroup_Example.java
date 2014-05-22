package CalendarService;


import java.io.File;
import java.io.IOException;

import calendarBox.Tools;
import net.jxta.document.MimeMediaType;
import net.jxta.document.XMLElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.content.ContentServiceImpl;
import net.jxta.impl.peergroup.CompatibilityUtils;
import net.jxta.impl.peergroup.StdPeerGroup;
import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
import net.jxta.membership.MembershipService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.Module;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleImplAdvertisement;

public class _220_Creating_A_Custom_PeerGroup_Example {
    
    public static final String Name = "Example 220";
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    public static final String PeerGroupName = "Custom peer group name";
    public static final PeerGroupID CustPeerGroupID = IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, PeerGroupName.getBytes());
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);

            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Starting the network
            PeerGroup MyNetPeerGroup = MyNetworkManager.startNetwork();

            // Creating a child group with PSE
            PeerGroup ChildPeerGroup = MyNetPeerGroup.newGroup(
                    CustPeerGroupID,
                    createAllPurposePeerGroupImplAdv(),
                    PeerGroupName,
                    "Custom peergroup..."
                    );

            if (Module.START_OK != ChildPeerGroup.startApp(new String[0]))
                System.err.println("Cannot start custom peergroup");

            // Checking membership implementation
            MembershipService ChildGroupMembership = ChildPeerGroup.getMembershipService();

            Tools.PopInformationMessage(Name, "Custom group membership implementation:\n"
                + ChildGroupMembership.getClass().getSimpleName());

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();

        } catch (PeerGroupException Ex) {

            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

    public static ModuleImplAdvertisement createAllPurposePeerGroupImplAdv() {

        ModuleImplAdvertisement implAdv = CompatibilityUtils.createModuleImplAdvertisement(
            PeerGroup.allPurposePeerGroupSpecID, StdPeerGroup.class.getName(),
            "General Purpose Peer Group");

        // Create the service list for the group.
        StdPeerGroupParamAdv paramAdv = new StdPeerGroupParamAdv();

        // set the services
        paramAdv.addService(PeerGroup.endpointClassID, PeerGroup.refEndpointSpecID);
        paramAdv.addService(PeerGroup.resolverClassID, PeerGroup.refResolverSpecID);
        paramAdv.addService(PeerGroup.membershipClassID, PeerGroup.refMembershipSpecID);
        paramAdv.addService(PeerGroup.accessClassID, PeerGroup.refAccessSpecID);

        // standard services
        paramAdv.addService(PeerGroup.discoveryClassID, PeerGroup.refDiscoverySpecID);
        paramAdv.addService(PeerGroup.rendezvousClassID, PeerGroup.refRendezvousSpecID);
        paramAdv.addService(PeerGroup.pipeClassID, PeerGroup.refPipeSpecID);
        paramAdv.addService(PeerGroup.peerinfoClassID, PeerGroup.refPeerinfoSpecID);

        paramAdv.addService(PeerGroup.contentClassID, ContentServiceImpl.MODULE_SPEC_ID);

        // Insert the newParamAdv in implAdv
        XMLElement paramElement = (XMLElement) paramAdv.getDocument(MimeMediaType.XMLUTF8);
        implAdv.setParam(paramElement);

        return implAdv;

    }

}