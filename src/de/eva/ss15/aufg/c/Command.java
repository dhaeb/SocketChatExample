package de.eva.ss15.aufg.c;

import java.io.UnsupportedEncodingException;

/**
 * Iterface für alle Nachrichten, die zwischen den Clienten und dem Server ausgetauscht werden.
 * 
 * @author dhaeb
 *
 */
public interface Command {

	/**
	 * Unser definiertes Protokoll Suffix, mit welchem das Ende / der Anfang zweier Nachrichten identifiziert werden kann 
	 */
	public static final String PROTOCOL_SUFFIX = Constants.DELIMITER;

	String getContent();
	
	/**
	 * @return byterepräsentation des Objektes (ohne Java-Serialisierung, um Programm nicht Java-Abhängig zu machen
	 */
	default byte[] toBytes() {
		try {
			return (getContent() + PROTOCOL_SUFFIX).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}
	
	/**
	 * Broadcast Nachricht
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
	 * Nachricht an speziellen Nutzer 
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
	 * Wenn diese Nachricht bei einem Clienten ankommt, soll der Content der Nachricht ausgegeben werden, 
	 * welche die registrierten Nutzernamen enthält.
	 * 
	 * @author dhaeb
	 *
	 */
	public static class ListCommand extends MessageCommand {
		public ListCommand() {
			super("@LIST");
		}
	}
	
	/**
	 * Kommando zur Registrierung eines Nutzers.
	 * 
	 * @author dhaeb
	 *
	 */
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