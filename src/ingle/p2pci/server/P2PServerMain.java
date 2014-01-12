package ingle.p2pci.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServerMain {

	public static void main(String args[]) {
		ClientWorker clientHandler;
		ServerSocket MyServerSocket;
		Socket clientSocket;
		try {

			MyServerSocket = new ServerSocket(65401);
			CentralizedIndex index = new CentralizedIndex();
			while (true) {
				clientSocket = MyServerSocket.accept();
				clientHandler = new ClientWorker(
						clientSocket.getInputStream(),
						clientSocket.getOutputStream(), index);
				Thread t = new Thread(clientHandler);
				t.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
