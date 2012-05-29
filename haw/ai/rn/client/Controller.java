package haw.ai.rn.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class Controller {
	private Chat view;
	private Client client;
	private Thread userUpdater;
	
	public Controller() {
		userUpdater = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					synchronized (Controller.this) {
						Controller.this.updateClients();
					}
					System.out.println("Update Clients");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void setView(Chat view) {
		this.view = view;
	}
	
	public void connect(String server, String nick) {
		try {
			this.client = new Client(InetAddress.getByName(server), new MessageReceiver() {
			    @Override
			    public void receiveMessage(String msg) {
			        Controller.this.messageRecived(msg);
			    }
			});
			
			this.client.setName(nick);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userUpdater.start();
	}
	
	protected void updateClients() {
		Map<String, InetAddress> clients = this.client.getClients();
		StringBuilder list = new StringBuilder();
		for (Map.Entry<String, InetAddress> e : clients.entrySet()) {
			list.append(e.getKey()+"\n");
		}
		this.view.getNicks().setText(list.toString());
	}

	public void messageRecived(String message) {
		String old = this.view.getMessages().getText();
		this.view.getMessages().setText(old + message);
	}
	
	public void sendMessage(String message) {
		this.client.sendMessage(message);
	}

	public void run() {
		this.view.getFrame().setVisible(true);
	}
	
	public void close() {
		client.disconnect();
		userUpdater.stop();
		System.exit(0);
	}
}
