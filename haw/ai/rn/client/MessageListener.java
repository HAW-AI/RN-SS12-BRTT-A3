package haw.ai.rn.client;

import static haw.ai.rn.Protocol.CLIENT_PORT;
import static haw.ai.rn.Protocol.MAX_MSG_LEN;
import static haw.ai.rn.Protocol.MAX_NAME_LEN;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * The MessageListener will listen on the port Protocol.CLIENT_PORT for
 * incoming messages and will delegate them to its MessageReceivers.
 *
 */
public class MessageListener implements Runnable {
    private MessageReceiver receiver;
    private DatagramSocket socket;
    
    public MessageListener(MessageReceiver receiver) {
        this.receiver = receiver;
    }
    
    public void stop() {
        socket.close();
    }
    
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(CLIENT_PORT);
            
            // calculate buffer size
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < MAX_NAME_LEN; ++i) { nameBuilder.append('\0'); }
            
            StringBuilder msgBuilder = new StringBuilder();
            for (int i = 0; i < MAX_MSG_LEN; ++i) { msgBuilder.append('\0'); }
            
            String longestPossibleData = Client.dataForMessage(nameBuilder.toString(), msgBuilder.toString());
            
            byte[] buf = longestPossibleData.getBytes();
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            
            while (true) {
                // clear the buffer
                for (int i = 0; i < buf.length; ++i) { buf[i] = '\0'; }
                
                socket.receive(p);
                receiver.receiveMessage(new String(p.getData()));
            }
        } catch (SocketException e) {
            // socket closed
            // everything's okay
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
