package ingle.p2pci.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Iterator;

public class ClientWorker implements Runnable {

	private CentralizedIndex index;
	private BufferedReader clientInput;
	private PrintWriter clientOutput;
	private RequestHandler requestHandler;
	private String clientHostName;
	static final MessageParser messageParser = new MessageParser();

	public void process() throws IOException {

		if(requestHandler!=null)
		{
			requestHandler.process(index);
			String reply = requestHandler.respondToClient(index);
			sendResponseToClient(reply);

		}
		else
			sendResponseToClient("400 Bad Request");
		
	}

	public void sendResponseToClient(String message) throws IOException {
		System.out.println(message);
		clientOutput.println(message);
		clientOutput.flush();
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;

	}

	public ClientWorker(InputStream clientInput, OutputStream clientOutput,
			CentralizedIndex index) {
		this.index = index;
		this.clientInput = new BufferedReader(
				new InputStreamReader(clientInput));
		this.clientOutput = new PrintWriter(clientOutput);

	}

	public void tryRun() throws IOException, SocketException {
		try {
			while (true) {
				// messageParser.parse(clientInput);
				setRequestHandler(messageParser.parse(clientInput));
				// setRequest(request);
				process();
			}
		} catch (SocketException socket) {
			removePeerData();
		}
	}

	public void removePeerData() {
		System.out.println("Handling socket exception");
		if (requestHandler != null)
			index.removePeerData(requestHandler.getRequest());
		for (final Integer key : index.getTableOfRFCs().keySet()) {
			RFCNode node = index.getTableOfRFCs().get(key);
			if (node != null) {
				Iterator<PeerNode> iterator = node.getPeersWithThisRFC()
						.iterator();
				while (iterator.hasNext()) {
					PeerNode peerNew = iterator.next();
					System.out.println(node.getRFCNo() + " "
							+ node.getRFCTitle() + " " + peerNew.getHostname()
							+ " " + peerNew.getUploadPort() + "\n");
				}
				
			}
			else
				System.out.println("RFC empty");

		}
	}

	public void run() {
		try {
			tryRun();

		}
		// System.out.println(inputLine);

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
