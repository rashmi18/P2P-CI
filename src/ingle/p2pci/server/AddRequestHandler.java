package ingle.p2pci.server;

import java.util.Iterator;

public class AddRequestHandler implements RequestHandler {

	private Request request;

	
	public Request getRequest()
	{
		return request;
	}
	@Override
	public void process(CentralizedIndex index) {
		// TODO Auto-generated method stub
		System.out.println("In add request");

		PeerNode peer = index.addPeer(request);
		

		index.addRFC(request, peer);
		for (final Integer key : index.getTableOfRFCs().keySet()) {
			RFCNode node = index.getTableOfRFCs().get(key);
			Iterator<PeerNode> iterator = node.getPeersWithThisRFC()
					.iterator();
			while (iterator.hasNext()) {
				PeerNode peerNew = iterator.next();
				System.out.println(node.getRFCNo() + " "
						+ node.getRFCTitle() + " "
						+ peerNew.getHostname() + " "
						+ peerNew.getUploadPort() + "\n");
			}
			//System.out.println("RFCNode in hashtable is " + node.getRFCTitle());
		}

	}

	@Override
	public String respondToClient(CentralizedIndex index) {
		String responseMessage = new String("P2P-CI/1.0 200 OK\r\n");
		return responseMessage;
		
	}

	public void setRequest(Request request) {
		this.request = request;
	}

}
