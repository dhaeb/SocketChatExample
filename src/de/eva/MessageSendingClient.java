package de.eva;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

public class MessageSendingClient {

	private static final String PROTOCOL_EXTENTION = "ZZZ";
	private static final String DEFAULT_ENCODING = "UTF-8";

	public static void main(String[] args) throws IOException {
		try (Socket clientSideSocket = new Socket("localhost", ChatServer.PORT)) {
			OutputStream osToServer = clientSideSocket.getOutputStream();
			System.out.println("Insert message for server: ");
			sendMessage(osToServer);
			osToServer.flush();
			String answer = StreamUtils.readRequest(clientSideSocket.getInputStream());
			System.out.println(answer);
		}
		System.out.println("Successfully transfered message!");
	}

	private static void sendMessage(OutputStream osToServer) throws IOException, UnsupportedEncodingException {
		try(Scanner sc = new Scanner(System.in)){
			osToServer.write((sc.next() + PROTOCOL_EXTENTION).getBytes(DEFAULT_ENCODING));
		}
	}

}
