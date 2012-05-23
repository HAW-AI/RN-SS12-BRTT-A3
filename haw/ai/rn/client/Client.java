package haw.ai.rn.client;

import static haw.ai.rn.Protocol.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

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
}
