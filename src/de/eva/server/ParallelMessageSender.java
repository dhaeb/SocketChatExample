package de.eva.server;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import de.eva.server.pojo.Message;
import de.eva.sockets.MessageSender;

public class ParallelMessageSender extends Thread {

	private BlockingQueue<Message> messages;

	public ParallelMessageSender(BlockingQueue<Message> messages) {
		this.messages = messages;
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
