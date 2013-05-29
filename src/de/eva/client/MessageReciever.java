package de.eva.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ServerSocketFactory;

import de.eva.StreamUtils;

public class MessageReciever extends Thread {

	private ServerSocket server;
	private DateFormat df = SimpleDateFormat.getInstance();

	public MessageReciever(int port) throws IOException {
		server = ServerSocketFactory.getDefault().createServerSocket(port);
		System.out.println("[DEBUG] Started reciever thread");
		this.setDaemon(true);
	}

	@Override
	public void run() {
		while (true) {
			try (Socket serverSideSocket = server.accept()) {
				String request = StreamUtils.readRequest(serverSideSocket.getInputStream());
				if(request.endsWith("ZZZ")){
					request = request.substring(0, request.length() - 3);
				}
				System.out.println(df.format(new Date()) + " " + request);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
