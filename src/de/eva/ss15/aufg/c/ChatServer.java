package de.eva.ss15.aufg.c;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

	public static void main(String[] args) throws IOException {
		int port = 4002;
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		CommandHandler commandHandler = new CommandHandler();
		try(ServerSocket serverSocket = new ServerSocket(port)){
			System.out.println("Server gestartet auf port " + port);
			while(!Thread.interrupted()){
				try {
					Socket clientConnectionSocket = serverSocket.accept();
					threadPool.execute(new ChatClientHandler(clientConnectionSocket, commandHandler, threadPool));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
