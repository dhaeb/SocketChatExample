package de.eva;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

public class ChatServer {

	public static final int PORT = 7777;

	private static ServerSocketFactory SOCKET_FACTORY = ServerSocketFactory.getDefault();
	public static final String PROTOCOL_MESSAGE_END = "ZZZ";
	private static final String CLOSING_MESSAGE = "close()ZZZ";
	
	private int port;
	private ServerSocket serverSocket;

	public ChatServer(int port) {
		this.port = port;
	}

	public void startServer() throws IOException {
		serverSocket = SOCKET_FACTORY.createServerSocket(port);
		System.out.println("Server is listening on port " + port + "...");
		boolean isShutdown = false;
		while (!isShutdown) {
			// JAVA 7 try-with resources statement!
			try (Socket serverSideSocket = serverSocket.accept()) {
				System.out.println("Incoming client request from " + serverSideSocket.getInetAddress().getHostName() + "!");
				String request = StreamUtils.readRequest(serverSideSocket.getInputStream());
				System.out.println("Request is: " + (request.endsWith(PROTOCOL_MESSAGE_END) ? request.substring(0, request.length() - 3): request));
				isShutdown = shutDownSignalSend(request);
				if(isShutdown){
					request = "sended shutdown message, server will halt now";
				}
				sendEcho(serverSideSocket, request);
			}

			System.out.println("finished read progress");
		}
		System.out.println("Server process finished");
	}

	private void sendEcho(Socket serverSideSocket, String request) throws IOException {
		PrintWriter writer = new PrintWriter(serverSideSocket.getOutputStream());
		writer.append("ECHO: " + request);
		writer.flush();
	}

	private boolean shutDownSignalSend(String request) {
		return CLOSING_MESSAGE.equals(request);
	}

	public static void main(String[] args) throws IOException {
		new ChatServer(PORT).startServer();
	}

}
