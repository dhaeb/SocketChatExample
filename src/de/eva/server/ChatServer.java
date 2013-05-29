package de.eva.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ServerSocketFactory;

import de.eva.StreamUtils;
import de.eva.server.pojo.Client;
import de.eva.server.pojo.Message;

public class ChatServer {

	private static ServerSocketFactory SOCKET_FACTORY = ServerSocketFactory.getDefault();
	public static final String PROTOCOL_MESSAGE_END = "ZZZ";
	
	private ServerSocket serverSocket;
	private List<Client> clients = new ArrayList<>();
	private LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>(100);
	private int port;

	public ChatServer(int port) {
		this.port = port;
	}

	public void startServer() throws IOException, InterruptedException {
		System.out.println("Server is listening on port " + port + "...");
		serverSocket = SOCKET_FACTORY.createServerSocket(port);
		System.out.println("Message Sender has started");
		new ParallelMessageSender(this).start();
		boolean isShutdown = false;
		while (!isShutdown) {
			// JAVA 7 try-with resources statement!
			try (Socket serverSideSocket = serverSocket.accept()) {
				System.out.println("Incoming client request from " + serverSideSocket.getInetAddress().getHostName() + "!");
				String request = StreamUtils.readRequest(serverSideSocket.getInputStream());
				new InputHandler(this, serverSideSocket, request).parseInput();
				System.out.println("finished managing progress");
			}
		}
		System.out.println("Server process finished");
	}

	public LinkedBlockingQueue<Message> getMessages() {
		return messages;
	}

	public List<Client> getClients() {
		return clients;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new ChatServer(8081).startServer();
	}

}
