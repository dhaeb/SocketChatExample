package de.eva.sockets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import de.eva.server.pojo.Client;
import de.eva.server.pojo.Message;

public class MessageSender {

	private static final String DEFAULT_ENCODING = "UTF-8";
	private Message message;
	private Client targetClient;

	public MessageSender(Message message) {
		this.message = message;
		targetClient = message.getTargetClient();
	}
	
	public void sendMessage() throws IOException {
		try(Socket socket = new Socket(targetClient.getHost(), targetClient.getPort())){
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), DEFAULT_ENCODING));
			pw.println(message.getMsg());
			pw.flush();
		}
	}
}
