package haw.ai.rn.client;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        System.out.println("Start client");
        
        Controller controller = new Controller();
        controller.setView(new Chat());
        controller.run();
        controller.connect("localhost", "Salz");
        controller.sendMessage("hello");
        controller.sendMessage("World!");
        
        /*
        try {
            InetAddress serverIp = InetAddress.getLocalHost();

            Client client = new Client(serverIp, new MessageReceiver() {
                @Override
                public void receiveMessage(String msg) {
                    System.out.println(">> " + msg);
                }
            });
            
            Scanner sc = new Scanner(System.in);
            System.out.println("What's your name?");
            client.setName(sc.nextLine());
            
            while (client.isConnected()) {                
                String cmd = sc.nextLine().trim();
                
                if (cmd.equalsIgnoreCase("info")) {
                    System.out.println(client.getClients());
                } else if (cmd.equalsIgnoreCase("bye")) {
                    client.disconnect();
                } else {
                    client.sendMessage(cmd);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        
        System.out.println("Stopping client");

    }
}
