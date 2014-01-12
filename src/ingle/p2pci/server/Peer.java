package ingle.p2pci.server;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gargi
 * Date: 11/16/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Peer Class
 * Represents a Peer in the system
 */
public class Peer {
    String hostname;
    int uploadPort;
    Map<Integer,ArrayList<String>> rfcMap;
    ArrayList<String> hostRFCList;
    Socket socToServer;

    Peer(String hostname, final int port)
    {
        this.hostname = hostname;
        this.uploadPort = port;
        this.rfcMap = new HashMap<Integer,ArrayList<String>>();    //String in form hostname:uploadport::
        setHostRFCList();
        new Thread(new Runnable(){
            public void run (){
                startUploadServer(port);}
        }).start();
    }

    /**
     * Starts the upload server port for this peer , at which it can wait for requests from other peers
     * @param port
     */
    private void startUploadServer(int port)
    {
        try
        {
            ServerSocket uploadServer = new ServerSocket(port);
            this.uploadPort = uploadServer.getLocalPort();
            System.out.println("Also the Upload Server for this Peer Is Awaiting Requests On Port:" +this.uploadPort);
            while(true)
            {
                //Listen for a connection request from other peer
                Socket peerConnection = uploadServer.accept();

                //Get the request from the peer
                //Process the request using a new thread
                PeerRequestHandler request = new PeerRequestHandler(peerConnection);
                Thread thread = new Thread(request);
                thread.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    public void getRFCRequest(int rfcNo, String peer)  throws UnknownHostException , IOException
    {
            String peerToken[] = peer.split(":");
            InetAddress address = InetAddress.getByName(peerToken[0]);
            Socket peerSocket = new Socket(address, Integer.parseInt(peerToken[1]));
            System.out.println("Connection Established");

            //Send Msg to Server
            DataOutputStream os = new DataOutputStream(peerSocket.getOutputStream());
            os.writeBytes("GET RFC "+rfcNo+" P2P-CI/1.0\r\nHost " + peerToken[0]+ " \r\nOS "+System.getProperty("os.name")+"\r\n\r\n");

            //Read Msg from the Server
            readGetResponse(peerSocket.getInputStream(), rfcNo);

            peerSocket.close();
    }

    public void connectToServer(String hostname)  {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Enter Proper Hostname");
        }
        try {
            socToServer = new Socket(address, 65401);
        } catch (IOException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Connection Refused. Try Again");
        }
        System.out.println("Connection Established With the BootStrap Server");
    }

    public void addRFCRequest() throws UnknownHostException , IOException
    {
        //Send Msg to Server
        PrintWriter os = new PrintWriter(socToServer.getOutputStream());

        Iterator it = hostRFCList.iterator();
        if(it != null )
        {
            while(it.hasNext())
            {
                os.println("ADD RFC "+(String)it.next()+" P2P-CI/1.0\r\nHost: " + hostname+ "\r\nPort: "+uploadPort+"\r\nTitle: A new pr Official ICP");
                //os.println("ADD RFC 456 P2P-CI/1.0\r\nHost: new.host\r\nPort: 6767\r\nTitle: A new pr Official ICP");
                os.flush();
                readAddResponse(socToServer.getInputStream());
            }
        }
        //os.println("ADD RFC 456 P2P-CI/1.0\r\nHost: new.host\r\nPort: 6767\r\nTitle: A new pr Official ICP");
        //os.flush();
        //readAddResponse(socToServer.getInputStream());

        //Read Response from the Server
        //readAddResponse(socToServer.getInputStream());
    }

    /**
     *
     * @param rfcNo
     * @throws UnknownHostException
     * @throws IOException
     */
    public void addRFCRequest(int rfcNo) throws UnknownHostException , IOException
    {
        //Send Msg to Server
        String rfcTitle = "Title";
        PrintWriter os = new PrintWriter(socToServer.getOutputStream());
        os.println("ADD RFC "+rfcNo+" P2P-CI/1.0\r\nHost: " + hostname+ "\r\nPort: "+uploadPort+"\r\nTitle: "+rfcTitle);
        os.flush();

        //Read Response from the Server
        readAddResponse(socToServer.getInputStream());
    }

    public void lookupRFCRequest(int rfcNo) throws UnknownHostException , IOException
    {
        //Send Msg to Server
        String rfcTitle = "Title";
        PrintWriter os = new PrintWriter(socToServer.getOutputStream());
        os.println("LOOKUP RFC "+rfcNo+" P2P-CI/1.0\r\nHost: " + hostname+ "\r\nPort: "+uploadPort+"\r\nTitle: "+rfcTitle);
        os.flush();

        //Read Response from the Server
        readLookupResponse(socToServer.getInputStream(), rfcNo, rfcTitle);
    }

    public void listRFCRequest() throws UnknownHostException , IOException
    {
        //Send Msg to Server
        PrintWriter os = new PrintWriter(socToServer.getOutputStream());
        os.println("LIST ALL P2P-CI/1.0\r\nHost: " + hostname+ "\r\nPort: "+uploadPort);
        os.flush();

        //Read Response from the Server
        readListResponse(socToServer.getInputStream());
    }

    /**
     *
     * @param is
     * @param rfcNo
     * @throws IOException
     */
    private void readGetResponse(InputStream is, int rfcNo) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        File file = null;
        String version = null;
        String statusCode = null;
        String statusPhrase = null;
        String[] tokens;
        String date , serverOS , contType;
        long lastModifiedDate = 0, contLength  = 0;

        //Read the first line : Status Line
        String line = br.readLine();
        tokens = line.split(" ");
        version = tokens[0];
        statusCode = tokens[1];
        statusPhrase = tokens[2];

        if(Integer.parseInt(statusCode) == 200 && statusPhrase.equalsIgnoreCase("OK"))
        {
            //Read the further lines of response
            line = br.readLine();   //Reads the DATE
            tokens = line.split(" ");
            if(tokens.length == 2) {
                date = tokens[1];
            }

            line = br.readLine();   //Reads the OS
            tokens = line.split(" ");
            if(tokens.length == 2) {
                serverOS = tokens[1];
            }

            line = br.readLine();    //Reads the LAST MODIFIED DATE for file
            tokens = line.split(" ");
            if(tokens.length == 2) {
                lastModifiedDate = Integer.parseInt(tokens[1]);
            }

            line = br.readLine();   //Reads the CONTENT LENGTH
            tokens = line.split(" ");
            if(tokens.length == 2) {
                contLength = Integer.parseInt(tokens[1]);
            }

            line = br.readLine();    //Reads the CONTENT TYPE
            tokens = line.split(" ");
            if(tokens.length == 2) {
                contType = tokens[1];
            }

            line = br.readLine();    //Blank Line

            //Create the file
            file = new File("rfc"+rfcNo+".txt");
            boolean newFile = file.createNewFile();
            if(!newFile)
            {   int i = 1;
                do
                {
                    file = new File("rfc"+rfcNo+"copy"+i+".txt");
                    i++;
                }while(!file.createNewFile());
            }

            file.setLastModified(lastModifiedDate);

            System.out.println("Downloading the File");
            FileOutputStream fileOS = new FileOutputStream(file.getName());
            byte[] buffer = new byte[1024];
            int bytes = 0;
            while ((bytes = is.read(buffer)) > 0 ) {
                fileOS.write(buffer, 0, bytes);
            }
            fileOS.flush();
            if(file.length() != contLength)
            {
                System.out.println("File Transfer Incomplete");
                addRFCRequest(rfcNo);
            }
            else
            {
                System.out.println("File Downloaded Completely");
            }
            fileOS.close();
            }
        else
        {
            //Do not Read the further response
            printErrorCodes(statusCode);
        }
    }

    private void readAddResponse(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        File file = null;
        String version = null;
        String statusCode = null;
        String statusPhrase = null;
        String[] tokens;
        String date , serverOS , contType;
        long lastModifiedDate = 0, contLength  = 0;

        //Read the first line : Status Line
        String line = br.readLine();
        tokens = line.split(" ");
        version = tokens[0];
        statusCode = tokens[1];
        statusPhrase = tokens[2];

        if(Integer.parseInt(statusCode) == 200 && statusPhrase.equalsIgnoreCase("OK"))
        {
            //TODO: Do we want to check the next Line
            System.out.println("Add RFC Request Successful");
        }
        else
        {
            //Do not Read the further response
            printErrorCodes(statusCode);
        }
    }

    private void readLookupResponse(InputStream is, int rfcNo, String rfcTitle) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        File file = null;
        String version = null;
        String statusCode = null;
        String statusPhrase = null;
        String[] tokens;
        String date , serverOS , contType;
        long lastModifiedDate = 0, contLength  = 0;

        //Read the first line : Status Line
        String line = br.readLine();
        System.out.println(line);
        tokens = line.split(" ");
        version = tokens[0];
        statusCode = tokens[1];
        statusPhrase = tokens[2];

        if(Integer.parseInt(statusCode) == 200 && statusPhrase.equalsIgnoreCase("OK"))
        {
            //Read the Second Blank Line
            //br.readLine();

            //Make a new list , after reading the whole msg , put it into the map
            ArrayList<String> list = new ArrayList<String>();

            //Read the next lines till you dont get a null
            while(!(line = br.readLine()).isEmpty())
            {
            	System.out.println(line);
            	tokens = line.split(" ");
            int lineLen = tokens.length;
            if(!(lineLen < 2))
            {
            list.add(tokens[lineLen-2]+":"+tokens[lineLen-1]);
            //System.out.println("Peer added to the list:"+tokens[lineLen-2]+":"+tokens[lineLen-1]);
            }
            }

            rfcMap.put(rfcNo,list);
            System.out.println("LookUp Request Successful");
        }
        else
        {
            //Do not Read the further response
            printErrorCodes(statusCode);
        }
    }

    private void readListResponse(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        File file = null;
        String version = null;
        String statusCode = null;
        String statusPhrase = null;
        String[] tokens;
        String date , serverOS , contType;
        long lastModifiedDate = 0, contLength  = 0;

        //Read the first line : Status Line
        String line = br.readLine();
        tokens = line.split(" ");
        version = tokens[0];
        statusCode = tokens[1];
        statusPhrase = tokens[2];

        if(Integer.parseInt(statusCode) == 200 && statusPhrase.equalsIgnoreCase("OK"))
        {
            //Read the Second Blank Line
            //br.readLine();

            rfcMap.clear(); //clears the previous mappings
            ArrayList<String> list = null;
            String currNum = null;
            String lastNum = null;

            //Read for the first record
            list = new ArrayList<String>();
            line = br.readLine();
            tokens = line.split(" ");
            lastNum = tokens[0];
            int lineLen = tokens.length;
            if(!(lineLen < 2))
            {
                list.add(tokens[lineLen-2]+":"+tokens[lineLen-1]);
                //System.out.println("Peer added to the list:"+tokens[lineLen-2]+":"+tokens[lineLen-1]);
            }

            //Read the next lines till you dont get a null
            //consider records for one rfc num are sent consecutively
            while(!(line = br.readLine()).isEmpty())
            {
                tokens = line.split(" ");
                currNum = tokens[0];
                if(currNum.equalsIgnoreCase(lastNum))
                {
                lineLen = tokens.length;
                if(!(lineLen < 2))
                    {
                    list.add(tokens[lineLen-2]+":"+tokens[lineLen-1]);
                    //System.out.println("Peer added to the list:"+tokens[lineLen-2]+":"+tokens[lineLen-1]);
                    }
                }
                else
                {//start for new rfc num
                 rfcMap.put(Integer.parseInt(lastNum),list);
                 list = new ArrayList<String>();
                 lastNum = tokens[0];
                 lineLen = tokens.length;
                     if(!(lineLen < 2))
                     {
                     list.add(tokens[lineLen-2]+":"+tokens[lineLen-1]);
                     //System.out.println("Peer added to the list:"+tokens[lineLen-2]+":"+tokens[lineLen-1]);
                     }
                }
            }

            //put the last list
            rfcMap.put(Integer.parseInt(lastNum),list);
            System.out.println("LIST Request Successful");
        }
        else
        {
            //Do not Read the further response
            printErrorCodes(statusCode);
        }
    }

    private void printErrorCodes(String statusCode) {
        if(Integer.parseInt(statusCode) == 400)
        {
            //BAD REQUEST
            System.out.println("BAD REQUEST");
        }
        if(Integer.parseInt(statusCode) == 404)
        {
            //FILE NOT FOUND
            System.out.println("FILE NOT FOUND");
        }

        //VERSION MISMATCH
        if(Integer.parseInt(statusCode) == 505)
        {
            //FILE NOT FOUND
            System.out.println("P2P-CI VERSION NOT SUPPORTED");
        }
    }

    public void setHostRFCList()
    {
        hostRFCList = new ArrayList<String>();
        String dir =(System.getProperty("user.dir"));
        String src = dir.replace('\\','/');
        File f = new File(src);
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String tokens[] = name.split("\\.(?=[^\\.]+$)");
                return (tokens[0].startsWith("rfc")|tokens[0].startsWith("RFC"));
            }
        });

        for(File file:matchingFiles)
        {
            String str = file.getName().split("\\.(?=[^\\.]+$)")[0];
            if(str.matches("(rfc|RFC)[\\d]+"))
            {
                hostRFCList.add( str.substring(3));
                //System.out.println("inside"+str.substring(3));
            }

        }
    }
}



