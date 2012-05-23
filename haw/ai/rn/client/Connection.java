package haw.ai.rn.client;

import static haw.ai.rn.Protocol.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Connection implements Runnable {
    private Socket socket;
    private boolean connected;
    
    
    public Connection(InetAddress serverAddr) throws IOException {
        this.socket = new Socket(serverAddr, SERVER_PORT);
        this.connected = true;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        while (connected) {
        }
        try {
            System.out.println(String.format("close connection for %s", socket.getInetAddress()));
            socket.close();
            socket = null;
        } catch (IOException e) {
            System.out.println(String.format("Can't close connection to %s", socket.getInetAddress()));
        }
        finally {
            socket = null;
        }
    }
    
    public void close() {
        this.connected = false;
    }

    public String request(String msg) {
        String response;
        
        try {
            socket.getOutputStream().write((msg+MSG_DELIMITER).getBytes());
            response = new Scanner(socket.getInputStream()).useDelimiter(MSG_DELIMITER).next().trim();
        } catch (IOException e) {
            e.printStackTrace();
            response = "ERROR connection not available";
        }
        
        return response;
    }
}
