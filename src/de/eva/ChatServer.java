package de.eva;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

public class ChatServer {

	private static ServerSocketFactory SOCKET_FACTORY = ServerSocketFactory.getDefault();
	
	private static final int MESSAGE_BUFFER_SIZE = 4096;
	private static final String PROTOCOL_MESSAGE_END = "ZZZ";
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
			// JAVA 7!
			try (Socket serverSideSocket = serverSocket.accept()) {
				System.out.println("Incoming client request from " + serverSideSocket.getInetAddress().getHostName() + "!");
				String request = readRequest(serverSideSocket.getInputStream());
				System.out.println("Request is: " + (request.endsWith(PROTOCOL_MESSAGE_END) ? request.substring(0, request.length() - 3): request));
				isShutdown = shutDownSignalSend(request);
			}
			// serverSideSocket.close	();

			System.out.println("finished read progress");
		}
		System.out.println("Server process finished");
	}

	private String readRequest(InputStream is) throws IOException {
		String request = "";
		int countOfReadedBytes = 0;
		byte[] buffer = new byte[MESSAGE_BUFFER_SIZE];
		while (request.lastIndexOf(PROTOCOL_MESSAGE_END) == -1
			   && countOfReadedBytes > -1) {
			countOfReadedBytes = is.read(buffer, countOfReadedBytes, buffer.length - countOfReadedBytes);	// if Message Buffer Size is exceeded,
																						// a ArrayOutOfBoundException is thrown
			request = new String(buffer, "UTF-8");
		}
		return request.trim();
	}

	private boolean shutDownSignalSend(String request) {
		return CLOSING_MESSAGE.equals(request);
	}

	public static void main(String[] args) throws IOException {
		new ChatServer(666).startServer();
	}

}
