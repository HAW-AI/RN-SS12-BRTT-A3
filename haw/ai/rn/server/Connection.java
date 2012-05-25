package haw.ai.rn.server;

import static haw.ai.rn.Protocol.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Connection implements Runnable {
	private Socket socket = null;
	private Server server;
	
	public Connection(Socket socket, Server server) {
		this.setSocket(socket);
		this.server = server;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		boolean connected = true;
		while (connected) {
			try {
				String msg = new Scanner(getSocket().getInputStream()).useDelimiter(MSG_DELIMITER).next();
				if (msg.toLowerCase().trim().startsWith("new ")) {
					// NEW <name>  - Anmelden
					String name = msg.substring(4).trim();
					if (name.matches(NAME_PATTERN)) {
						synchronized ("add client") {
							if (server.addClient(name, this)) {
								respond("OK");
							}
							else {
								respond("ERROR name taken");
							}
						}
					}
					else {
						respond("ERROR name invalid");
					}
				}
				else if (msg.toLowerCase().trim().startsWith("info")) {
					// INFO        - Teilnehmerliste zurückschicken.
					StringBuilder response = new StringBuilder();
					Map<Connection, String> clients = server.getClients();
					response.append("LIST");
					response.append(String.format(" %d", clients.size()));
					for (Map.Entry<Connection, String> e : clients.entrySet()) {
					    String ip = e.getKey().clientAddress().toString();
					    ip = ip.substring(ip.indexOf('/') + 1); // strip host name
						response.append(String.format(" %s %s", ip, e.getValue()));
					}
					respond(response.toString());
				}
				else if (msg.toLowerCase().trim().equals("bye")) {
					// BYE         - Abmelden
					respond("BYE");
					System.out.println(String.format("%s requests end of connection", getSocket().getInetAddress()));
					connected = false;
				}
				else {
					System.out.println(String.format("ERROR \"%s\" is unknown", msg.trim()));
					respond(String.format("ERROR \"%s\" is unknown", msg.trim()));
				}
			}
			catch (NoSuchElementException e) {
				connected = false;
				System.out.println(String.format("Client %s offline?", socket.getInetAddress()));
			}
			catch (Exception e) {
				connected = false;
				e.printStackTrace();
			}
		}
		try {
			System.out.println(String.format("close connection for %s", socket.getInetAddress()));
			server.removeClient(this);
			socket.close();
			socket = null;
		} catch (IOException e) {
			System.out.println(String.format("Can't close connection to %s", socket.getInetAddress()));
		}
		finally {
			socket = null;
		}
	}

	private Socket getSocket() {
		return socket;
	}
	
	private void respond(String msg) {
		try {
			getSocket().getOutputStream().write((msg+MSG_DELIMITER).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress clientAddress() {
	    return socket.getInetAddress();
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((server == null) ? 0 : server.hashCode());
        result = prime * result + ((socket == null) ? 0 : socket.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Connection other = (Connection) obj;
        if (server == null) {
            if (other.server != null)
                return false;
        } else if (!server.equals(other.server))
            return false;
        if (socket == null) {
            if (other.socket != null)
                return false;
        } else if (!socket.equals(other.socket))
            return false;
        return true;
    }
}
