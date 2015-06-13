package de.eva.ss15.aufg.c;

import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

final class ChatClientHandler implements Runnable {
	
	private final Socket clientConnectionSocket;
	private final CommandHandler commandHandler;
	private final CommandInterpreter interpreter = new CommandInterpreter();
	private ExecutorService threadPool;

	public ChatClientHandler(Socket clientConnectionSocket, CommandHandler commandHandler, ExecutorService threadPool) {
		this.clientConnectionSocket = clientConnectionSocket;
		this.commandHandler = commandHandler;
		this.threadPool = threadPool;
	}

	@Override
	public void run() {
		try {
			try(Scanner scanner = new Scanner(clientConnectionSocket.getInputStream())){
				scanner.useDelimiter(Constants.DELIMITER);
				while(!Thread.currentThread().isInterrupted()){
					String currentCommand = scanner.next();
					System.out.println("interpreting... " + currentCommand);
					Command interpretable = interpreter.interpret(currentCommand.trim());
					threadPool.execute(commandHandler.handle(interpretable, clientConnectionSocket));
				}
			}
		} catch (Exception e) {
			commandHandler.removeUserSocket(clientConnectionSocket);
			System.err.println(e.getMessage());
		}
	}
}