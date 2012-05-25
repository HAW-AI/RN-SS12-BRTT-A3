package haw.ai.rn.client;

import static haw.ai.rn.Protocol.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    private String name;
    private Connection connection;

    public Client(InetAddress serverAddr) throws IOException {
        this.connection = new Connection(serverAddr);
    }

    public String getName() {
        return name;
    }

    public boolean setName(String name) {
        if (isValidName(name)) {
            this.name = name;
            String resp = connection.request("new " + name);
            return resp.equals("OK");
        } else {
            return false;
        }
    }
    
    public void disconnect() {
        String resp = connection.request("BYE");
        if (resp.equals("bye")) {
            connection.close();
        }
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
        
        String resp = connection.request("info");
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
}
