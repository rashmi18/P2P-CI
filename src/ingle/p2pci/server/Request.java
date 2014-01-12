package ingle.p2pci.server;

public class Request {

	private String requestType;
	private String host;
	private Integer port;
	private Integer RFCNo;
	private String Title;
	private String version;

	String getRequestType() {
		return requestType;
	}

	void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	String getHost() {
		return host;
	}

	void setHost(String host) {
		this.host = host;
	}

	Integer getPort() {
		return port;
	}

	void setPort(Integer port) {
		this.port = port;
	}

	Integer getRFCNo() {
		return RFCNo;
	}

	void setRFCNo(Integer rFCNo) {
		RFCNo = rFCNo;
	}

	String getTitle() {
		return Title;
	}

	void setTitle(String title) {
		Title = title;
	}

	 String getVersion() {
		return version;
	}

	 void setVersion(String version) {
		this.version = version;
	}

}
