package de.eva.ss15.aufg.c;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try(Scanner s = new Scanner(System.in)){
			System.out.println("Give host and port of chat server...");
			System.out.println("host:");
			String host = s.next();
			System.out.println("port:");
			int port = s.nextInt();
			try(Socket serverConnection = new Socket(host, port)){
				System.out.println("Connection established... your name is ....");
				String name = s.next();
				sendCommand(serverConnection, new Command.RegisterCommand(name));
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
