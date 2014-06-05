package de.eva;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer2 {

	private int port;
	private ServerSocket server;
	private boolean isShutdown;

	public ChatServer2(int port) throws IOException {
		this.port = port;
		server = new ServerSocket();
	}

	public void startServer() throws IOException{
		while (!isShutdown) {
			server.bind(new InetSocketAddress("localhost", port));
			Socket serverSideSocket = server.accept();
			try {
				InputStream is = serverSideSocket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				System.out.println(br.readLine());
			} finally {
				serverSideSocket.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new ChatServer2(ChatServer.PORT).startServer();
	}
	
}
