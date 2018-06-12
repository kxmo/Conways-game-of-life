package parsing.terminal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import datastructures.Pair;

public class Parser
{
	private final Options options;
	private Optional<String> helpMessage;

	public Parser()
	{
		options = new Options();
		helpMessage = Optional.empty();
	}

	public void addOption(String shortName,
			String longName,
			String description,
			boolean hasArg,
			boolean required)
	{
		Option o = new Option(shortName, longName, hasArg, description);
		o.setRequired(required);
		options.addOption(o);
	}

	/**
	 * Name -> associated String value.
	 * 
	 * Java's type system does not allow heterogeneous lists or methods that
	 * return heterogeneous types. The ideal for this function would be:
	 * Map<OptionName, OptionType> where OptionType is determined at runtime
	 * (enums are very close but cannot have type params) and OptionType differs
	 * between the OptionNames.
	 * @param args The command line arguments to parse.
	 * @return A map from option long name to the string version of the option value.
	 * The String version of the value will need to be parsed separately. This function
	 * merely extracts the value.
	 * Optional.empty() is returned if there was an error parsing the options.
	 */
	public Optional<Map<String, String>> parse(String[] args)
	{
		Pair<Map<String, String>, String[]> map = parse(args, false);
		
		if (!map.left().isEmpty())
		{
			return Optional.of(map.left());
		}
		else
		{
			return Optional.empty();
		}
	}
	
	public Pair<Map<String, String>, String[]> parseReturnUnused(String[] args)
	{
		return parse(args, true);
	}
	
	/**
	 * Parse arguments according to the options, returning the results and any unused
	 * arguments.
	 * If args could not be parsed the help message is set and an empty map and list is returned. 
	 * @param args
	 * @return
	 */
	private Pair<Map<String, String>, String[]> parse(String[] args, boolean returnUnused)
	{
		CommandLineParser parser = new DefaultParser();
		Map<String, String> argToResult = new HashMap<>();

		try
		{
			CommandLine result = parser.parse(options, args, returnUnused);

			for (Option option : result.getOptions())
			{
				argToResult.put(option.getLongOpt(), option.getValue());
			}

			return new Pair<Map<String,String>, String[]>(argToResult, result.getArgs());
		}
		catch (ParseException e)
		{
			helpMessage = Optional.of(e.getMessage());
			
			String[] empty = {}; // Don't appear to be able to do this inline
			// If a parse exception happened then we don't have meaningful remaining args
			return new Pair<Map<String,String>, String[]>(Collections.emptyMap(), empty);
		}
	}

	public Optional<String> getHelpMessage()
	{
		return helpMessage;
	}

	public static Optional<Integer> parseInt(String s)
	{
		try
		{
			int result = Integer.parseInt(s);
			return Optional.of(result);
		}
		catch (NumberFormatException e)
		{
			return Optional.empty();
		}
	}

	public String getUsage()
	{
		HelpFormatter f = new HelpFormatter();
		StringWriter s = new StringWriter();
		f.printHelp(new PrintWriter(s), 80, " ", "Options: <short>, <long> <arg>  <desc>", options, 2, 2, "");
		return s.toString();
	}
}
