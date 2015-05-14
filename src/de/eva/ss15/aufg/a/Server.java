package de.eva.ss15.aufg.a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	static final int PORT = 4001;

	public static void main(String[] args) throws IOException {
		ServerSocket ssocket = new ServerSocket(PORT);
		System.out.println("Der Server laeuft auf Port " + PORT);
		try(Socket clientSocket = ssocket.accept()){
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println(br.readLine());
		}
		ssocket.close();
	}
}
