package ImpApServer;

import java.io.*;
import java.net.*;

import org.ini4j.*;

import java.util.*;

public class Server implements Runnable {
    // some constants
    public static final String CONFIG_FILE = "config.ini";
    public static int PORT_NUMBER;
    public static String PATH;
    public static String CONTEXT;
    // all of the connections we have made to clients...
    private Set<ClientHandler> connections = new HashSet<ClientHandler>();

    public static void main(String[] args) {
        System.setProperty("line.separator", "\r\n");
        //
        String test=" blah@test.test p";
        System.out.println("Base64 for \"" + test + "\" is: " + Base64Coder.encodeString(test));
        //
        Ini ini = new Ini();
        try {
            ini.load(new FileReader(CONFIG_FILE));
            Map<String, String> serverOptions = ini.get("server");
            Server.PORT_NUMBER = Integer.parseInt(serverOptions.get("port"));
            Server.PATH = serverOptions.get("feed_directory");
            Server.CONTEXT = serverOptions.get("context");
            ClientHandler.UIDVALIDITY = Integer.parseInt(serverOptions.get("uidvalidity"));
        }
        catch (Exception err) {
            System.err.println("Unable to start server, could not load config file config.ini");
            err.printStackTrace();
            System.exit(1);
        }

        new Server();
    }

    public Server() {
        try {
            Thread serverListener = new Thread(this);
            serverListener.start();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server socket waiting for connections on port number " + PORT_NUMBER);
            while (true) {
                Socket client = serverSocket.accept();
                ClientHandler temp = new ClientHandler(client, this);
                this.connections.add(temp);
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

}
