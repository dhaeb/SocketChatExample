package de.eva.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import de.eva.StreamUtils;
import de.eva.server.pojo.Client;
import de.eva.server.pojo.Message;
import de.eva.sockets.MessageSender;

public class ChatClient {

	private static final String PROTOCOL_EXTENTION = "ZZZ";
	private static final String DEFAULT_ENCODING = "UTF-8";

	private boolean isFinished;
	
	private String clientName;
	private Client server;
	
	private MessageReciever messageReciever;
	private Scanner inputStreamScanner;
	private static final int CLIENT_DEFAULT_PORT = 9051;

	public ChatClient() throws Exception {
		inputStreamScanner = new Scanner(System.in);
	}

	public void registerAtServer() throws Exception {
		String registerString = getRegistrationInformation();
		messageReciever = new MessageReciever(CLIENT_DEFAULT_PORT);
		messageReciever.start();
		try (Socket clientSideSocket = new Socket(server.getHost(), server.getPort())) {
			String answer = registerClient(registerString, clientSideSocket);
			System.out.println(answer);
			if (answer.contains("successful")) {
				System.out.println("Use @LIST for get to know, who is on.\n" +
									"A message starting with @<USERNAME> is send execlusivly to the specified user." +
									"\nUse END to close the chat.");
			} else {
				System.err.println("answer: " + answer);
				throw new IOException(answer);
			}
		}
	}

	private String getRegistrationInformation() throws Exception {
		System.out.println("Welcome! Please specify the host and port of the server!");
		System.out.println("Host:");
		String serverHost = inputStreamScanner.next();
		System.out.println("Port:");
		int serverPort = inputStreamScanner.nextInt();
		System.out.println("Name:");
		clientName = inputStreamScanner.next();
		String clientHost = InetAddress.getByName("localhost").getCanonicalHostName();
		server = createClientFrom(serverHost, serverPort, clientName);
		String registerString = String.format("%s\u00001@REGISTER %s:%d", clientName, clientHost, CLIENT_DEFAULT_PORT);
		System.out.println(registerString);
		return registerString;
	}

	private Client createClientFrom(String serverHost, int serverPort, String clientName) throws UnknownHostException {
		Client c = new Client();
		c.setHost(InetAddress.getByName(serverHost));
		c.setName(clientName);
		c.setPort(serverPort);
		return c;
	}

	private String registerClient(String registerString, Socket clientSideSocket) throws IOException, UnsupportedEncodingException {
		System.out.println("sending registration message...");
		OutputStream osToServer = clientSideSocket.getOutputStream();
		sendMessage(registerString, osToServer);
		osToServer.flush();
		String answer = StreamUtils.readRequest(clientSideSocket.getInputStream());
		return answer;
	}

	public void sendUserInput() throws Exception {
		inputStreamScanner.skip(" *");
		while (!isFinished) {
			String userInput = getUserInput();
			if (userInput.equals("END")) {
				isFinished = true;
				sendCloseMessage();
			} else {
				if("@CLOSE".equals(userInput)){
					sendCloseMessage();
					isFinished = true;
					startClient();
				} else {
					userInput = clientName + "\u00001" + userInput;
					new MessageSender(new Message(userInput + "ZZZ", server)).sendMessage();
				}
			}
		}
	}

	private void sendCloseMessage() throws IOException {
		Message msg = new Message(clientName + "\u00001" + "@CLOSE" + "ZZZ", server);
		new MessageSender(msg).sendMessage();
	}

	private String getUserInput() {
		return inputStreamScanner.nextLine().trim();
	}

	private void sendMessage(String sendable, OutputStream osToServer) throws IOException, UnsupportedEncodingException {
		osToServer.write((sendable + PROTOCOL_EXTENTION).getBytes(DEFAULT_ENCODING));
		osToServer.flush();
	}

	public static void main(String[] args) throws Exception {
		startClient();
	}

	private static void startClient() throws Exception {
		ChatClient chatClient = new ChatClient();
		chatClient.registerAtServer();
		chatClient.sendUserInput();
	}
}
