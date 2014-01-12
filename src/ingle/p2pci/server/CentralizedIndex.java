package ingle.p2pci.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class CentralizedIndex {

	/* Key: HostName Value: Node with the Peer */
	private Hashtable<String, PeerNode> tableOfPeers;

	/* Key: RFC Number Value: Node with the RFC */
	private Hashtable<Integer, RFCNode> tableOfRFCs;

	public CentralizedIndex() {
		setTableOfPeers(new Hashtable<String, PeerNode>());
		setTableOfRFCs(new Hashtable<Integer, RFCNode>());
	}

	public RFCNode lookUpRFC(Request request) {
		return tableOfRFCs.get(request.getRFCNo());

	}

	public void removePeerData(Request request) {
		PeerNode peerToBeDeleted = tableOfPeers.get(request.getHost());

		tableOfPeers.remove(peerToBeDeleted);
		
		Iterator<Integer> iterator = peerToBeDeleted.getRFCNo().iterator();
		while (iterator.hasNext()) {
			int rfcNo = iterator.next();
			RFCNode rfcNode = tableOfRFCs.get(rfcNo);
			if (rfcNode != null) {
				System.out.println("Removing from RFC  "+rfcNode.getRFCNo());
				if(tableOfRFCs.get(rfcNo).getPeersWithThisRFC().remove(peerToBeDeleted))
						System.out.println("Peer removed");
				if(tableOfRFCs.get(rfcNo).getPeersWithThisRFC().size()==0)
					tableOfRFCs.remove(tableOfRFCs.get(rfcNo));
				
			}

		}
	}

	public PeerNode addPeer(Request request) {
		if (!getTableOfPeers().containsKey(request.getHost())) // Add peer to
																// list
		{
			// Create a PeerNode
			PeerNode peernode = new PeerNode();
			peernode.setHostname(request.getHost());
			System.out.println("RFC nullpointer" + request.getRFCNo());
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(request.getRFCNo());
			peernode.setRFCNo(a);
			peernode.getRFCNo().add(request.getRFCNo());
			peernode.setUploadPort(request.getPort());

			getTableOfPeers().put(request.getHost(), peernode);
			return peernode;

		}
		else
		{
			PeerNode peer = getTableOfPeers().get(request.getHost());
			peer.getRFCNo().add(request.getRFCNo());
			return peer;
		}
		

	}

	public void addRFC(Request request, PeerNode peer) {

		if (!getTableOfRFCs().containsKey(request.getRFCNo())) // RFC not
																// present
		{
			RFCNode rfcnode = new RFCNode();
			rfcnode.setRFCTitle(request.getTitle());
			System.out.println("Title added "+request.getTitle());
			rfcnode.setRFCNo(request.getRFCNo());

			rfcnode.setPeersWithThisRFC(new ArrayList<PeerNode>());
			rfcnode.getPeersWithThisRFC().add(peer);
			getTableOfRFCs().put(request.getRFCNo(), rfcnode);
			System.out.println("In RFCTable added"+request.getRFCNo());

		} else {
			// Just add peer node to arraylist
			RFCNode temp;
			temp = getTableOfRFCs().get(request.getRFCNo());
			if(!temp.getPeersWithThisRFC().contains(peer))//same peernode not added twice
			temp.getPeersWithThisRFC().add(peer);
		}

	}

	Hashtable<String, PeerNode> getTableOfPeers() {
		return tableOfPeers;
	}

	void setTableOfPeers(Hashtable<String, PeerNode> tableOfPeers) {
		this.tableOfPeers = tableOfPeers;
	}

	Hashtable<Integer, RFCNode> getTableOfRFCs() {
		return tableOfRFCs;
	}

	void setTableOfRFCs(Hashtable<Integer, RFCNode> tableOfRFCs) {
		this.tableOfRFCs = tableOfRFCs;
	}

}
