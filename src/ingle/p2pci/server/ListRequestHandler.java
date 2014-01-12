package ingle.p2pci.server;

import java.util.Iterator;

public class ListRequestHandler implements RequestHandler {

	private Request request;

	public Request getRequest() {
		return request;
	}

	@Override
	public void process(CentralizedIndex index) {
		// TODO Auto-generated method stub
		System.out.println("In list request");
		for (final Integer key : index.getTableOfRFCs().keySet()) {
			RFCNode node = index.getTableOfRFCs().get(key);
			System.out.println("RFCNode in hashtable is " + node.getRFCTitle());
		}

	}

	@Override
	public String respondToClient(CentralizedIndex index) {
		// TODO Auto-generated method stub

		StringBuffer responseMessage = new StringBuffer(request.getVersion());
		StringBuffer records = new StringBuffer();
		String status=null;
		for (final Integer key : index.getTableOfRFCs().keySet()) {
			RFCNode node = index.getTableOfRFCs().get(key);
			if (node != null) {
				 status = new String(" 200 OK\n");
				Iterator<PeerNode> iterator = node.getPeersWithThisRFC()
						.iterator();
				while (iterator.hasNext()) {
					PeerNode peer = iterator.next();
					records.append(node.getRFCNo() + " "
							+ node.getRFCTitle() + " " + peer.getHostname()
							+ " " + peer.getUploadPort() + "\n");
				}

			} else {
				 status = new String(request.getVersion()
						+ " 404 Not Found\n");
			}
			

		}
		responseMessage.append(status + records);
		System.out.println("Response message is " + responseMessage);

		return responseMessage.toString();
	}

	public void setRequest(Request request) {
		this.request = request;
	}

}
