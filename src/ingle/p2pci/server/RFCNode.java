package ingle.p2pci.server;
import java.util.ArrayList;

public class RFCNode {

	private String RFCTitle;
	private Integer RFCNo;
	private ArrayList<PeerNode> peersWithThisRFC;
	String getRFCTitle() {
		return RFCTitle;
	}
	void setRFCTitle(String rFCTitle) {
		RFCTitle = rFCTitle;
	}
	ArrayList<PeerNode> getPeersWithThisRFC() {
		return peersWithThisRFC;
	}
	void setPeersWithThisRFC(ArrayList<PeerNode> peersWithThisRFC) {
		this.peersWithThisRFC = peersWithThisRFC;
	}
	 Integer getRFCNo() {
		return RFCNo;
	}
	 void setRFCNo(Integer rFCNo) {
		RFCNo = rFCNo;
	}
}
