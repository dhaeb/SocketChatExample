package de.eva.ss15.aufg.c;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter {

	private Pattern REGISTER_PATTERN = Pattern.compile("@REGISTER\\s(.+):(\\d+)\\s(.+)");
	private Pattern LIST_PATTERN = Pattern.compile("@LIST");
	private Pattern TO_SPECIAL_USER_PATTERN = Pattern.compile("@(.+?)\\s(.+)");
	private Pattern REST = Pattern.compile("(.+)");
	
	public Command interpret(String currentCommand) {
		return tryPatternInOrder(currentCommand,
				new Pair<Pattern, Function<Matcher, Command>>(REGISTER_PATTERN, (m) -> new Command.RegisterCommand(m.group(1), Integer.parseInt(m.group(2)), m.group(3))),
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
