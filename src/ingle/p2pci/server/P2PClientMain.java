	package ingle.p2pci.server;
	import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
	
	public class P2PClientMain {
	
		public static void main(String[] args) {
	
			try {
				Socket MyClient;
				Socket newSocket;
				ServerSocket mySocket = new ServerSocket(9191);

				System.out.println(args[1]);

				MyClient = new Socket(args[0], Integer.parseInt(args[1]));

				PrintWriter output = new PrintWriter(
						MyClient.getOutputStream());
				InputStreamReader input = new InputStreamReader(
						MyClient.getInputStream());
				String inp;
				BufferedReader buffer = new BufferedReader(input);
				//BufferedWriter buffout = new BufferedWriter(output);
//				buffout.write("ADD RFC 123 P2P-CI/1.0\r\nHost: thishost.csc.ncsu.edu\r\nPort: 5678\r\nTitle: A Proferred Official ICP\r\n\r\n");
				
				output.println("AD RFC 123 P2P-CI/1.0\r\nHost: this.host.csc.ncsu.edu\r\nPort: 5678\r\nTitle: A Proferred Official ICP");
				output.flush();
				inp=buffer.readLine();
				System.out.println(inp);
				
				output.println("ADD RFC 456 P2P-CI/1.0\r\nHost: new.host\r\nPort: 6767\r\nTitle: A new pr Official ICP");
				output.flush();
				inp=buffer.readLine();
				System.out.println(inp);
				
				output.println("ADD RFC 456 P2P-CI/1.0\r\nHost: other.host\r\nPort: 8989\r\nTitle: A new pr Official ICP");
				output.flush();
				inp=buffer.readLine();
				System.out.println(inp);

				output.println("LOOKUP RFC 123 P2P-CI/1.0\r\nHost: this.host.csc.ncsu.edu\r\nPort: 5678\r\nTitle: A new pr Official ICP");
				output.flush();
				while((inp=buffer.readLine())!=null)
				System.out.println(inp);

				output.println("LIST ALL P2P-CI/1.0\r\nHost: this.host.csc.ncsu.edu\r\nPort: 5678");
				output.flush();
				while((inp=buffer.readLine())!=null)
					System.out.println(inp);

				
				while(true)
				{
				

				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	
	}
