package ingle.p2pci.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: gargi
 * Date: 11/16/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerRequestHandler implements Runnable {

    int rfcNo ;
    String hostname;
    String os;
    String version;
    Socket socket;
    boolean badRequestFlag = false;

    public PeerRequestHandler(Socket socket)
    {
        this.socket = socket;
    }
    @Override
    public void run() {
        try
        {
            processRequest();
        }   catch (Exception e)
        {
           e.printStackTrace();
        }
    }

    private void processRequest() throws IOException {
        System.out.println("Process the Request");

        File file = null;
        boolean fileNotFoundFlag = false;
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        //Parse the msg
        parseRequest();

        //Search for the requested RFC - may be present in any file type
        File[] possibleFiles = findRFCPaths();

        if(possibleFiles.length != 0)
        {
            //Take the first format type
            for(File path:possibleFiles)
            {
               file = path;
                break;
            }
        }
        else
        {
          fileNotFoundFlag = true;
        }

        String statusLine = null;

        if(badRequestFlag)
        {
            statusLine = "P2PCI/1.0 400 BAD REQUEST\r\n";
            os.writeBytes(statusLine);
        }
        else if(fileNotFoundFlag)
        {
            statusLine = "P2PCI/1.0 404 NOT FOUND\r\n";
            os.writeBytes(statusLine);
        }
        else
        {
            statusLine = "P2PCI/1.0 200 OK\r\n";
            os.writeBytes(statusLine);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            os.writeBytes("Date: "+sdf.format(new Date())+"\r\n");
            os.writeBytes("Last-Modified: "+sdf.format(file.lastModified())+"\r\n");
            os.writeBytes("OS: "+System.getProperty("os.name")+"\r\n");
            os.writeBytes("Content-Length: "+file.length()+"\r\n");
            os.writeBytes("Content-Type: "+getContentTypeForFile(file)+"\r\n");
            os.writeBytes("\r\n");
            sendFileData(os,file);
        }
        System.out.println("Completed Processing the Request");
        os.close();
        socket.close();
    }

    private void parseRequest() throws IOException {
        //Read Msg from Client and Parse it
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        //Read First Line
        line = br.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
        while (stringTokenizer.hasMoreElements()) {
            String command = stringTokenizer.nextElement().toString();
            if(command == null || !command.equalsIgnoreCase("GET"))
            {
                badRequestFlag = true;
            }
            String rfc = stringTokenizer.nextElement().toString();
            if(rfc != null && rfc.equalsIgnoreCase("RFC"))
                rfcNo = Integer.parseInt(stringTokenizer.nextElement().toString());
            else badRequestFlag = true;
            version = stringTokenizer.nextElement().toString();
        }
        //Read second Line
        line = br.readLine();
        stringTokenizer = new StringTokenizer(line, " ");
        while (stringTokenizer.hasMoreElements()) {
            String client = stringTokenizer.nextElement().toString();
            if(client.equalsIgnoreCase("HOST"))
            {
                hostname = stringTokenizer.nextElement().toString();
                if(hostname == null)
                {
                    badRequestFlag = true;
                }
            }
        }
        //Read third Line
        line = br.readLine();
        stringTokenizer = new StringTokenizer(line, " ");
        while (stringTokenizer.hasMoreElements()) {
            String clientOS = stringTokenizer.nextElement().toString();
            if(clientOS.equalsIgnoreCase("OS"))
            {
                os = stringTokenizer.nextElement().toString();
                if(os == null)
                    badRequestFlag = true;
            }
        }
    }

    public File[] findRFCPaths()
    {
        //File f = new File("C:/Users/gargi/IdeaProjects/IPProject");
        String dir =(System.getProperty("user.dir"));
        String src = dir.replace('\\','/');
        File f = new File(src);
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String tokens[] = name.split("\\.(?=[^\\.]+$)");
                return tokens[0].equalsIgnoreCase("rfc"+String.valueOf(rfcNo));
            }
        });

        return matchingFiles;
    }

    private static String getContentTypeForFile(File file)
    {
        if (file.getName().endsWith("pdf"))
        {
            return "pdf/pdf";
        }
        else return "text/text" ;//default
    }

    private void sendFileData(DataOutputStream os , File file) throws IOException {
        FileInputStream fileIS = new FileInputStream(file.getName());
        //System.out.println("sending this file");
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fileIS.read(buffer)) != -1) {
            //System.out.println(buffer);
            os.write(buffer, 0, bytes);
        }
    }
}

