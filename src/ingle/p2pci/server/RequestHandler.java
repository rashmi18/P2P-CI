package ingle.p2pci.server;

public interface RequestHandler {
	
	public Request getRequest();
	public void setRequest(Request request);
	public void process(CentralizedIndex index);
	public String respondToClient(CentralizedIndex index);
}
