package de.eva.ss15.aufg.c;

import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * Kontrollfluss zur Überwachung eines Sockets auf Eingaben.
 * Bindet einen ganzen Thread an sich - auch wenn dieser durch I/O teilweise nur wartet.
 * Die Chat-Anwendung muss daher so viele Threads wie user bereithalten, 
 * damit es nicht zu Engpaessen in der Verarbeitung kommen kann.
 * 
 * Hier waere ein event-basierte Verarbeitung (wenn eine neue Nachricht eintrifft, nutze einen Thread zur Bearbeitung) effektiver.
 * 
 * @author dhaeb
 *
 */
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
			try(Scanner scanner = new Scanner(clientConnectionSocket.getInputStream())){ // zum ueberwachen, ob Kommandos von Client gesendet werden 
				scanner.useDelimiter(Constants.DELIMITER); // protokoll beachten (ZZZ)!
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