package de.eva.ss15.aufg.c;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Einfacher String zu Command Umwandler. 
 * Wird von Server und Client verwendet, 
 * um gesendete Strings über das Netzwerk bzw. Nutzereingaben in ein Command zu transferieren.
 * 
 * @author dhaeb
 *
 */
public class CommandInterpreter {
	
	private Pattern REGISTER_PATTERN = Pattern.compile("@REGISTER\\s(.+)");
	private Pattern LIST_PATTERN = Pattern.compile("@LIST");
	private Pattern TO_SPECIAL_USER_PATTERN = Pattern.compile("@(.+?)\\s(.+)");
	private Pattern REST = Pattern.compile("(.*)");
	
	/**
	 * Erzeugt aus einem String ein command. 
	 * Ein <code>@List</code> erzeugt beispielsweise ein ListCommand.
	 * 
	 * @param currentCommand
	 * @return Das interpretierte Command-Object, erzeugt aus dem String
	 */
	public Command interpret(String currentCommand) {
		return tryPatternInOrder(currentCommand,
				new Pair<Pattern, Function<Matcher, Command>>(REGISTER_PATTERN, (m) -> new Command.RegisterCommand(m.group(1))),
				new Pair<Pattern, Function<Matcher, Command>>(LIST_PATTERN, (m) -> new Command.ListCommand()),
				new Pair<Pattern, Function<Matcher, Command>>(TO_SPECIAL_USER_PATTERN, (m) -> new Command.ToSpecialUserCommand(m.group(2), m.group(1))),
				new Pair<Pattern, Function<Matcher, Command>>(REST, (m) -> new Command.MessageCommand(m.group(1)))
		);
						
	}

	@SafeVarargs
	private final Command tryPatternInOrder(String currentCommand, Pair<Pattern, Function<Matcher, Command>>... patterns) {
		for(Pair<Pattern, Function<Matcher, Command>> p : patterns){
			Matcher m = p._1.matcher(currentCommand);
			if(m.matches()){
				return p._2.apply(m);
			}
		}
		throw new IllegalArgumentException(currentCommand);
	}
	
}
