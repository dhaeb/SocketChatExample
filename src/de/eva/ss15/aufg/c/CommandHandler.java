package de.eva.ss15.aufg.c;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.eva.ss15.aufg.c.Command.ListCommand;
import de.eva.ss15.aufg.c.Command.MessageCommand;
import de.eva.ss15.aufg.c.Command.RegisterCommand;
import de.eva.ss15.aufg.c.Command.ToSpecialUserCommand;

// Thread safe!!!
public class CommandHandler {

	private Map<Socket, String> registeredSockets = new ConcurrentHashMap<>();
	private Map<String, Socket> registeredUsers = new ConcurrentHashMap<>();
	
	private Collection<String>getUserList(){
		return registeredSockets.values();
	}
	
	public Runnable handle(Command command, Socket from) {
		boolean userIsRegisteredOrWantsToRegister = registeredSockets.containsKey(from) || command instanceof RegisterCommand;
		if(userIsRegisteredOrWantsToRegister){
			return handleCommands(command, from);
		} else {
			return () -> {
				try {
					sendMessage("You need to register before sending messages!", from.getOutputStream());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};
		}
	}

	private Runnable handleCommands(Command command, Socket from) {
		final String content = command.getContent();
		if(command instanceof ListCommand){
			return () -> {
				try {
					OutputStream os = from.getOutputStream();
					sendMessage("[Server] These users are registered: " + String.join(", ", getUserList()), os);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};
		} else if(command instanceof RegisterCommand){
			return () -> {
				try {
					RegisterCommand ccommand = (RegisterCommand)command;
					String userName = ccommand.getName();
					boolean isUsernameAlreadyTaken = registeredUsers.containsKey(userName);
					boolean isSocketAlreadyRegistered = registeredSockets.containsKey(from); 
					if(isUsernameAlreadyTaken){
						String message = String.format("The user name %s is already taken - retry registration with other username", userName);
						sendMessage(message, from.getOutputStream());
					} if(isSocketAlreadyRegistered) {
						sendMessage("You cannot register twice!", from.getOutputStream());
					} else {
						registeredSockets.put(from, userName);
						registeredUsers.put(userName, from);
						String reply = String.format("[Server] You are registered %s! Type @LIST to see all users!", userName);  
						sendMessage(reply, from.getOutputStream());
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}	
			};
		} else if(command instanceof ToSpecialUserCommand){
			final ToSpecialUserCommand scommand = (ToSpecialUserCommand) command; 
			return () -> {
				Socket receiver = registeredUsers.get(scommand.getTo());
				if(receiver == null){
					System.err.println("Well this user was not found!");
					try {
						sendMessage("User " + scommand.getTo() + " not found!", from.getOutputStream());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						byte[] sendable = new ToSpecialUserCommand(getUserNotation(from)  + content, scommand.getTo()).toBytes();
						sendMessage(sendable, receiver.getOutputStream());
					} catch (SocketException e) {
						removeUserSocket(receiver);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			};
		} else if(command instanceof MessageCommand){
			return () -> {
				Set<Socket> keys = registeredSockets.keySet();
				keys.stream().forEach((s) -> {
					try {
						byte[] sendable = new MessageCommand(getUserNotation(from) + content).toBytes();
						sendMessage(sendable, s.getOutputStream());
					} catch (SocketException e) {
						removeUserSocket(s);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}	
				});
			};
		} else {
			throw new RuntimeException("There is serious truble...");
		}
	}

	public void removeUserSocket(Socket s) {
		String user = registeredSockets.get(s);
		System.out.println("removing user " + user);
		registeredUsers.remove(user);
		registeredSockets.remove(s);
	}

	private String getUserNotation(Socket from) {
		String currentUser = registeredSockets.get(from);
		if(currentUser == null){
			throw new IllegalArgumentException("user was not found!");
		} 
		return String.format("[%s] ", currentUser);
	}

	private void sendMessage(String message, OutputStream outputStream) throws IOException {
		sendMessage(new MessageCommand(message).toBytes(), outputStream);
	}
	
	private void sendMessage(byte[] bytes, OutputStream outputStream) throws IOException {
		BufferedOutputStream bof = new BufferedOutputStream(outputStream);
		bof.write(bytes);
		bof.flush();
	}

}
