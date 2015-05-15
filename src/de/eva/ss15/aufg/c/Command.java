package de.eva.ss15.aufg.c;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface Command {

	public static final String PROTOCOL_SUFFIX = Constants.DELIMITER;

	String getContent();
	default byte[] toBytes() {
		try {
			return (getContent() + PROTOCOL_SUFFIX).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}
	
	public static class MessageCommand implements Command {

		private String content;
		
		public MessageCommand(String content) {
			this.content = content;
		}

		@Override
		public String getContent() {
			return content ;
		}
		
	}
	
	public static class ToSpecialUserCommand extends MessageCommand {
		public ToSpecialUserCommand(String content, String to) {
			super(content);
			this.to = to;
		}

		private String to;

		public String getTo() {
			return to;
		}
		
		@Override
		public byte[] toBytes() {
			StringBuilder toSpecialUser = new StringBuilder();
			toSpecialUser.append("@");
			toSpecialUser.append(to);
			toSpecialUser.append(" ");
			toSpecialUser.append(getContent());
			toSpecialUser.append(PROTOCOL_SUFFIX);
			return (toSpecialUser.toString()).getBytes();
		}
	}
	
	public static class ListCommand extends MessageCommand {
		public ListCommand() {
			super("@LIST");
		}
	}
	
	public static class RegisterCommand extends MessageCommand {

		public static int DEFAULT_PORT = 4004;
		
		private String host;
		private int port;
		private String name;
		
		public RegisterCommand(String name){
			this(getLocalHostSilent().getHostAddress(), DEFAULT_PORT, name);
		}
		
		public RegisterCommand(String host, int port, String name)  {
			super(String.format("@REGISTER %s:%d %s", host, port, name));
			this.host = host;
			this.port = port;
			this.name = name;
		}

		private static InetAddress getLocalHostSilent() {
			try {
				return InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public String getName() {
			return name;
		}
		
	}
}