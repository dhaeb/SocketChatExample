package de.eva.server;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import de.eva.server.pojo.Message;
import de.eva.sockets.MessageSender;

public class ParallelMessageSender extends Thread {

	private LinkedBlockingQueue<Message> messages;

	public ParallelMessageSender(ChatServer server) {
		messages = server.getMessages();
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		while(true){
			try {
				System.out.println("sender waits for a message");
				Message currentMessage = messages.take();
				System.out.println("message recieved " + currentMessage.getMsg());
				new MessageSender(currentMessage).sendMessage();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
