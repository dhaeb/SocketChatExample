package de.eva.ss15.aufg.b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	static final int PORT = 4001;

	public static void main(String[] args) throws IOException {
		ServerSocket ssocket = new ServerSocket(PORT);
		System.out.println("Der Server laeuft auf Port " + PORT);
		try(Socket clientSocket = ssocket.accept()){
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String echoable = "ECHO " + br.readLine();
			System.out.println(echoable);
			OutputStream outputStream = clientSocket.getOutputStream();
			outputStream.write(echoable.getBytes());
		}
		ssocket.close();
	}
}
