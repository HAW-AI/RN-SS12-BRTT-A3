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
	private Map<String, InetAddress> clients = new HashMap<String, InetAddress>();
	
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

	public boolean addClient(String name, InetAddress inetAddress) {
		if (!isClientTaken(name)) {
			clients.put(name, inetAddress);
			System.out.println(String.format("New Client %s (%s)", name, inetAddress));
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isClientTaken(String name) {
		return clients.containsKey(name);
	}

	public Map<String, InetAddress> getClients() {
		return new HashMap<String, InetAddress>(this.clients);
	}
}
