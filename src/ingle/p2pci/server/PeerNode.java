package ingle.p2pci.server;
import java.util.ArrayList;


public class PeerNode {

	private String hostname;
	private Integer uploadPort;
	private ArrayList<Integer> RFCNo;
	String getHostname() {
		return hostname;
	}
	void setHostname(String hostname) {
		this.hostname = hostname;
	}
	Integer getUploadPort() {
		return uploadPort;
	}
	void setUploadPort(Integer uploadPort) {
		this.uploadPort = uploadPort;
	}
	ArrayList<Integer> getRFCNo() {
		return RFCNo;
	}
	void setRFCNo(ArrayList<Integer> rFCNo) {
		RFCNo = rFCNo;
	}
}
