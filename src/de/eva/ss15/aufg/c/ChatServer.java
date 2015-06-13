package de.eva.ss15.aufg.c;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Programmeinstiegspunkt für den Chatserver.
 * 
 * @author dhaeb
 *
 */
public class ChatServer {

	public static void main(String[] args) throws IOException {
		int port = 4002;
		int exepectedUserCount = 10;
		int countTaskHandlerThreads = 3;  
		ExecutorService threadPool = Executors.newFixedThreadPool(exepectedUserCount + countTaskHandlerThreads);
		CommandHandler commandHandler = new CommandHandler();
		try(ServerSocket serverSocket = new ServerSocket(port)){
			System.out.println("Server gestartet auf port " + port);
			while(!Thread.interrupted()){ // use the main thread to listen for new tcp connections (chat clients)
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
