package ingle.p2pci.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gargi
 * Date: 11/16/13
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Peer1 {
    public static void main(String[] args)
    {
        Peer peer = new Peer("localhost",0);
        try {
            System.out.println("This is Peer Program");
            boolean exitFlag = false;
            while(!exitFlag)
            {
                DisplayMenu();
                exitFlag = SelectionHandler(peer, exitFlag);
            }
            System.out.println("Peer is Going Down");
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean SelectionHandler(Peer peer, boolean exitFlag) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Integer option = Integer.parseInt(br.readLine());
        switch(option)
        {
            case 1:
                connectBootStrap(peer);
                break;
            case 2:
                peer.addRFCRequest();
                break;
            case 3:
                printPeerRFCMap(peer);
                break;
            case 4:
                getRFC(peer);
                break;
            case 5:
                lookupRFC(peer);
                break;
            case 6:
                listRFC(peer);
                break;
            case 7:
                peer.socToServer.close();
                System.out.println("Connection Terminated With the BootStrap Server");
                break;
            case 8:
                exitFlag=true;
                break;
            default:
                System.out.println("Invalid Selection");
        }
        return exitFlag;
    }

    private static void connectBootStrap(Peer peer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter hostname of the Bootstrap Server");
        String hostname = br.readLine();
        peer.connectToServer(hostname);
    }

    private static void printPeerRFCMap(Peer peer) {
        System.out.println("RFC MAPPINGS FOR PEER:");
        for (Map.Entry<Integer, ArrayList<String>> entry : peer.rfcMap.entrySet()) {
            Integer key = entry.getKey();
            System.out.println("RFCNUM: "+key);
            ArrayList<String> value = entry.getValue();
            Iterator it = value.iterator();
            while(it.hasNext())
            {
                System.out.println("Peers : "+(String)it.next());
            }
            System.out.println("****");
        }
    }

    private static void getRFC(Peer peer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter RFC Number To be Fetched");
        Integer rfcNum = Integer.parseInt(br.readLine());
        System.out.println("Enter hostname of the Peer To be Requested");
        String hostname = br.readLine();
        System.out.println("Enter upload port of the Peer To be Requested");
        Integer port = Integer.parseInt(br.readLine());

        peer.getRFCRequest(rfcNum, hostname+":"+port);
    }

    private static void lookupRFC(Peer peer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter RFC Number To be Looked Up");
        Integer rfcNum = Integer.parseInt(br.readLine());

        peer.lookupRFCRequest(rfcNum);
    }

    private static void listRFC(Peer peer) throws IOException {
        peer.listRFCRequest();
    }

    private static void DisplayMenu() {
        System.out.println("==============================================");
        System.out.println("   Select from Menu                           ");
        System.out.println("==============================================");
        System.out.println("        1 Connect to Bootstrap Server         ");
        System.out.println("        2 Add This Peer to the P2P System     ");
        System.out.println("        3 Show Current Peer-RFC Mappings      ");
        System.out.println("        4 Get RFC from a Peer                 ");
        System.out.println("        5 Lookup RFC                          ");
        System.out.println("        6 List All RFCs in the P2P System     ");
        System.out.println("        7 Terminate the Connection with Server");
        System.out.println("        8 Exit                                ");
        System.out.println("        Enter the Menu Number                 ");
        System.out.println("==============================================");
    }
    }






