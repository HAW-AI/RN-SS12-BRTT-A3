package haw.ai.rn.client;

import static haw.ai.rn.Protocol.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private String name;
    private Connection connection;
    private DatagramSocket outSocket;
    private MessageListener listener;
    private boolean isConnected;

    public Client(InetAddress serverAddr, MessageReceiver receiver) throws IOException {
        this.connection = new Connection(serverAddr);
        this.outSocket = new DatagramSocket();
        this.listener = new MessageListener(receiver);
        this.isConnected = true;
        
        new Thread(listener).start();
    }

    public String getName() {
        return name;
    }

    public boolean setName(String name) {
        if (isValidName(name)) {
            this.name = name;
            String resp = connection.request("NEW " + name);
            return resp.equals("OK");
        } else {
            return false;
        }
    }
    
    public void disconnect() {
        connection.request("BYE");
        listener.stop();
        connection.close();
        this.isConnected = false;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    boolean isValidName(String name) {
        return name.matches(NAME_PATTERN);
    }
    
    /**
     * List of users currently in the chat.
     * 
     * @return empty Map on failure, filled Map on success
     */
    Map<String, InetAddress> getClients() {
        Map<String, InetAddress> users = new HashMap<String, InetAddress>();
        
        String resp = connection.request("INFO");
        boolean error = resp.startsWith("ERROR");
        
        if (!error) {
            try {
                Scanner scanner = new Scanner(resp);
                scanner.skip("LIST");
                
                int numPairs = scanner.nextInt();
                for (int i = 0; i < numPairs; ++i) {
                    InetAddress ip = InetAddress.getByName(scanner.next());
                    String name = scanner.next();
                    users.put(name, ip);
                }
            } catch (Exception e) {
                error = true;
                e.printStackTrace();
            }
        }
        
        if (error) {
            users.clear();
        }
        
        return users;
    }
    
    /**
     * Send message to all the other chat clients.
     * if msg.length() > 60, the message will be split into multiple parts
     * 
     * @param msg the msg
     */
    public void sendMessage(String msg) {
        try {
            for (int offs = 0; offs < msg.length(); offs += MAX_MSG_LEN) {
                for (Map.Entry<String, InetAddress> e : getClients().entrySet()) {
                    int len = Math.min(MAX_MSG_LEN, msg.length() - offs);
                    String data = dataForMessage(getName(), msg.substring(offs, len));
                    outSocket.send(new DatagramPacket(data.getBytes(), offs, data.length(), e.getValue(), CLIENT_PORT));
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public static String dataForMessage(String name, String msg) {
        return String.format("%s: %s\n", name, msg.substring(0, Math.min(MAX_MSG_LEN, msg.length())));
    }
}
