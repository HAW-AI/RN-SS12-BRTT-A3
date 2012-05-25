package haw.ai.rn.client;

import java.net.InetAddress;

public class Application {
    public static void main(String[] args) {
        System.out.println("Start client");
                
        try {
            InetAddress serverIp = InetAddress.getByName("localhost");
            Client client = new Client(serverIp);
            client.setName("hans");
            System.out.println(client.getClients());
            Thread.sleep(2000);
            client.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("Stopping client");

    }
}
