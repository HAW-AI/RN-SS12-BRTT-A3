package haw.ai.rn.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
	private final ServerSocket socket;
	private final ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	
	// Connection:String instead of to String:InetAddress to be able to identify client name when the Connection dies unexpectedly 
	private Map<Connection, String> clients = new HashMap<Connection, String>();
	
	public Server(Integer port) throws IOException {
		socket = new ServerSocket(port);
	}
	
	public void run() {
		try {
			System.out.println("Waiting for incomming connection");
			while (true) {
				if (threads.getCorePoolSize() > threads.getActiveCount()) {
					threads.execute(new Connection(socket.accept(), this));
				}
			}
		} catch (IOException e) {
			threads.shutdown();
		}		
	}

	public boolean addClient(String name, Connection connection) {
		if (!isClientTaken(name)) {
		    if (clients.containsKey(connection)) {
                System.out.println(String.format("%s is now known as %s", clients.get(connection), name));
		    } else {
    			System.out.println(String.format("New Client %s (%s)", name, connection.clientAddress()));
		    }

            clients.put(connection, name);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void removeClient(Connection connection) {
        System.out.println(String.format("%s (%s) left", clients.get(connection), connection.clientAddress()));
	    clients.remove(connection);
	}

	public boolean isClientTaken(String name) {
		return clients.containsValue(name);
	}

	public Map<Connection, String> getClients() {
		return new HashMap<Connection, String>(this.clients);
	}
}
