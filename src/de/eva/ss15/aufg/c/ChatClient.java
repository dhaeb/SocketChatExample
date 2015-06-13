package de.eva.ss15.aufg.c;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Main class starting a chat client. 
 * Asks the user for the host and the port where the server is running.
 * If the connection have been successfully established, the programm asks for a user name. 
 * If this user name is available, the chat can be started.    
 * To close the application, type END into the chat client.
 * 
 * @author dhaeb
 *
 */
public class ChatClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try(Scanner s = new Scanner(System.in)){ // the object, that will handle the user input
			System.out.println("Give host and port of chat server...");
			System.out.println("host:");
			String host = s.next();
			System.out.println("port:");
			int port = s.nextInt();
			// connect to server, when failing, restart and try again
			try(Socket serverConnection = new Socket(host, port)){
				System.out.println("Connection established... your name is ....");
				String name = s.next();
				sendCommand(serverConnection, new Command.RegisterCommand(name)); // register at server
				CommandInterpreter ci = new CommandInterpreter(); 
				createAndStartListenerThread(serverConnection);
				String userinput = null;
				s.useDelimiter(System.getProperty("line.separator"));
				while(!(userinput = s.next()).equals("END")){ 
					sendCommand(serverConnection, ci.interpret(userinput));
				}
			}
		}
		
	}

	/**
	 * Starts a thread which interprets the incoming the commands from the server 
	 * 
	 * @param serverConnection
	 */
	private static void createAndStartListenerThread(Socket serverConnection) {
		CommandInterpreter ci = new CommandInterpreter();
		Thread listenerThread = new Thread(() -> {
			try(Scanner input = new Scanner(serverConnection.getInputStream())){
				input.useDelimiter(Constants.DELIMITER);
				while(!Thread.currentThread().isInterrupted()){
					Command commandFromServer = ci.interpret(input.next());
					String content = commandFromServer.getContent();
					if(commandFromServer instanceof Command.ToSpecialUserCommand){
						System.out.println("-->" + content);
					} else if(commandFromServer instanceof Command.MessageCommand || 
						   	      commandFromServer instanceof Command.ListCommand){ 
						System.out.println(content);
					} else {
						System.err.println("Got server command - cannot do something with it");
						System.err.println(content);
					}
				}
			} catch(IOException e){
				throw new RuntimeException(e);
			}
		});
		listenerThread.setDaemon(true);
		listenerThread.start();
	}

	private static void sendCommand(Socket serverConnection, Command c) throws IOException {
		OutputStream os = serverConnection.getOutputStream();
		os.write(c.toBytes());
		os.flush();
	}

}
