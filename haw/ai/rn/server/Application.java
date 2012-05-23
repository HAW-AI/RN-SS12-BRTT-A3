package haw.ai.rn.server;

import haw.ai.rn.Protocol;
import java.io.IOException;

public class Application {
/*
 * * Port 50000 auf eingehende Verbindung warten.
 */
	public static void main(String[] args) {
		try {
			System.out.println("Starting server");
			new Server(Protocol.SERVER_PORT).run();
		} catch (IOException e) {
			System.out.println("ups");
			e.printStackTrace();
		}
	}
}
