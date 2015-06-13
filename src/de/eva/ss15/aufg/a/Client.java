package de.eva.ss15.aufg.a;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Please type a message in: ");
		try(Scanner scanner = new Scanner(System.in)){
			scanner.useDelimiter("\n");
			String userInput = scanner.next();
			InetAddress localHost = InetAddress.getLocalHost();
			int port = Server.PORT;
			try(Socket socket = new Socket(localHost, port)){
				OutputStream os = socket.getOutputStream();
				byte[] bytes = userInput.getBytes();
				os.write(bytes);
			}
		}
	}
}
