package de.eva.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;

import de.eva.server.pojo.Client;
import de.eva.server.pojo.Message;
import de.eva.sockets.MessageSender;

public class InputHandler {

	private ChatServer server;
	private String request;
	private String name;
	private Socket serverSideSocket;

	public InputHandler(ChatServer server, Socket serverSideSocket, String request) {
		this.server = server;
		this.serverSideSocket = serverSideSocket;
		String[] req = request.split("\u00001");
		this.name = req[0];
		this.request = req[1];
	}

	public void parseInput() throws IOException, InterruptedException {
		System.out.println("[DEBUG] Request: " + request);
		if (request.startsWith("@REGISTER")) {
			registerClient();
		} else if (request.startsWith("@LIST")) {
			System.out.println("list");
			sendListOfRegisteredUsers();
		} else if (request.startsWith("@CLOSE")) {
			System.out.println("close");
			removeClient();
		} else {
			request = "[" + name + "]: " + request;
			if (request.startsWith("@")) {
				System.out.println("user");
				sendToSpecifiedUser();
			} else {
				if(!request.trim().isEmpty()){
					System.out.println("broatcast");
					sendBroadcast();
				} else {
					System.out.println("[DEBUG] discard empty request from " + name);
				}
			}
		}
	}

	private void registerClient() throws IOException {
		Client client = null;
		try {
			client = createClientFromInput(request);
			server.getClients().add(client);
			sendMessage("registration successful");
		} catch (NumberFormatException | UnknownHostException e) {
			System.err.println("[DEBUG] not able to register user " + name );
			e.printStackTrace();
			sendMessage(e.toString());
		}
	}

	private void sendMessage(String message) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(serverSideSocket.getOutputStream(), "UTF-8"));
		pw.println(message);
		pw.flush();
	}

	private void sendListOfRegisteredUsers() throws IOException {
		Message message = new Message("[SERVER]: Available Clients: ", getClientByName(name));
		MessageSender sender = new MessageSender(message);
		sender.sendMessage();
		for (Client currentClient : server.getClients()) {
			message.setMsg("[SERVER]: " + currentClient.getName());
			sender.sendMessage();
		}
	}

	private void removeClient() throws UnknownHostException {
		server.getClients().remove(getClientByName(name));
	}

	private void sendToSpecifiedUser() throws InterruptedException {
		try {
			String[] split = request.split(" ");
			String userName = split[0].substring(1);
			System.out.println("send request to user: " + userName);
			Client targetClient = getClientByName(userName);
			server.getMessages().put(new Message(request.substring(split[0].length()), targetClient));
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
	}

	private void sendBroadcast() throws InterruptedException {
		for (Client currentClient : server.getClients()) {
			server.getMessages().put(new Message(request, currentClient));
		}
	}

	private Client createClientFromInput(String request) throws NumberFormatException, UnknownHostException {
		String[] clientInfoArray = request.split(" "); // @REGISTER/CLOSE
														// host:port Name
		String[] hostPortArray = clientInfoArray[1].split(":");
		Client client = Client
				.createClient(name, 
							  InetAddress.getByName(hostPortArray[0]), 
							  Integer.parseInt(hostPortArray[1].substring(0, hostPortArray[1].length() -3)));
		return client;
	}

	private Client getClientByName(String name) {
		for (Client currentClient : server.getClients()) {
			if (currentClient.getName().equals(name)) {
				return currentClient;
			}
		}
		throw new NoSuchElementException(name);
	}

}
