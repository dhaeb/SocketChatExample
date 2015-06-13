package de.eva.ss15.aufg.c;

import java.io.UnsupportedEncodingException;

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
	
	/**
	 * Broadcast Fall
	 * 
	 * @author dhaeb
	 *
	 */
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
	
	/**
	 * Dedizierte Nutzernachricht 
	 * 
	 * @author dhaeb
	 *
	 */
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
	
	/**
	 * Dient dazu, die beim Server angemeldeten Nutzer zu erfahren.
	 * 
	 * @author dhaeb
	 *
	 */
	public static class ListCommand extends MessageCommand {
		public ListCommand() {
			super("@LIST");
		}
	}
	
	public static class RegisterCommand extends MessageCommand {

		private String name;
		
		public RegisterCommand(String name){
			super(String.format("@REGISTER %s", name));
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
}