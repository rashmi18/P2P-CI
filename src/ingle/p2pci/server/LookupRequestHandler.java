package ingle.p2pci.server;

import java.util.Iterator;

public class LookupRequestHandler implements RequestHandler {

	private Request request;
	RFCNode rfcNode;

	@Override
	public void process(CentralizedIndex index) {
		// TODO Auto-generated method stub
		this.rfcNode = index.lookUpRFC(request);

	}

	@Override
	public String respondToClient(CentralizedIndex index) {
		StringBuffer responseMessage=null;
		if (rfcNode != null) {
			 responseMessage = new StringBuffer(
					request.getVersion() + " 200 OK\n");
			Iterator<PeerNode> iterator = rfcNode.getPeersWithThisRFC()
					.iterator();
			while (iterator.hasNext()) {
				PeerNode peer = iterator.next();
				responseMessage.append(rfcNode.getRFCNo() + " "
						+ rfcNode.getRFCTitle() + " "
						+ peer.getHostname() + " "
						+ peer.getUploadPort() + "\n");
			}
			System.out.println("Response message is " + responseMessage);

		}
		else
		{
			responseMessage= new StringBuffer(request.getVersion() + " 404 Not Found\n");
		}
		return responseMessage.toString();

	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

}
