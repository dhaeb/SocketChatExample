package de.eva.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import de.eva.StreamUtils;
import de.eva.server.ChatServer;
import de.eva.server.pojo.Client;
import de.eva.server.pojo.Message;
import de.eva.sockets.MessageSender;

public class ChatClient {

	private static final String PROTOCOL_EXTENTION = "ZZZ";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static Client DEFAULT_CLIENT = null;

	static {
		try {
			DEFAULT_CLIENT = Client.createClient("server", InetAddress.getLocalHost(), ChatServer.PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private boolean isFinished;
	private String host;
	private Integer port;
	private MessageReciever messageReciever;

	private Scanner inputStreamScanner;
	private String name;

	public ChatClient() throws Exception {
		inputStreamScanner = new Scanner(System.in);
	}

	public void registerAtServer() throws Exception {
		String registerString = getRegistrationInformation();
		messageReciever = new MessageReciever(port);
		messageReciever.start();
		try (Socket clientSideSocket = new Socket(DEFAULT_CLIENT.getHost(), DEFAULT_CLIENT.getPort())) {
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
		String registerString = "@REGISTER ";
		System.out.println("Welcome! Please specify the host and port of the server!");
		System.out.println("Host:");
		host = inputStreamScanner.next();
		registerString += host + ":";
		System.out.println("Port:");
		port = inputStreamScanner.nextInt();
		registerString += port;
		System.out.println("Name:");
		name = inputStreamScanner.next();
		registerString = name + "\u00001" + registerString;
		System.out.println(registerString);
		return registerString;
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
					userInput = name + "\u00001" + userInput;
					new MessageSender(new Message(userInput + "ZZZ", DEFAULT_CLIENT)).sendMessage();
				}
			}
		}
	}

	private void sendCloseMessage() throws IOException {
		Message msg = new Message(name + "\u00001" + "@CLOSE" + "ZZZ", DEFAULT_CLIENT);
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
