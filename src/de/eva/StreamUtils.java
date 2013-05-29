package de.eva;

import java.io.IOException;
import java.io.InputStream;

import de.eva.server.ChatServer;

public class StreamUtils {

	private static final int MESSAGE_BUFFER_SIZE = 4096;
	
	public static String readRequest(InputStream is) throws IOException {
		String request = "";
		int countOfReadedBytes = 0;
		byte[] buffer = new byte[StreamUtils.MESSAGE_BUFFER_SIZE];
		while (request.lastIndexOf(ChatServer.PROTOCOL_MESSAGE_END) == -1
			   && countOfReadedBytes > -1
				) {
			countOfReadedBytes = is.read(buffer, countOfReadedBytes, buffer.length - countOfReadedBytes);	// if Message Buffer Size is exceeded,
																						// a ArrayOutOfBoundException is thrown
			request = new String(buffer, "UTF-8");
		}
		return request.trim();
	}

}
