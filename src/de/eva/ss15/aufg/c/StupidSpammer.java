package de.eva.ss15.aufg.c;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class StupidSpammer {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try(Socket s = new Socket("localhost", 4002)){
			OutputStream os = s.getOutputStream();
			Command.RegisterCommand registerCommand = new Command.RegisterCommand("Spammer");
			os.write(registerCommand.toBytes());
			os.flush();
			Timer t = new Timer();
			t.schedule(new TimerTask(){
				
				@Override
				public void run() {
					try {
						os.write(new Command.MessageCommand("I am spamming!").toBytes());
						os.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}, 0L, 100L);
			try(Scanner sc = new Scanner(s.getInputStream())){
				sc.useDelimiter(Constants.DELIMITER);
				while(true){
					System.out.println(sc.next());
				}
			}
		}
		}
}
