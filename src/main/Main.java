package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import datastructures.Pair;
import main.interfaces.AssetLoader;
import parsing.terminal.Parser;
import ui.gui.GraphicalAssetLoader;
import ui.interfaces.Displayable;
import ui.terminal.CommandLineAssetLoader;

public class Main
{
	public static void main(String[] args)
	{
		Parser mainArgs = new Parser();
		mainArgs.addOption("l", "loader", "The name of the asset loader", true, true);

		Pair<Map<String, String>, String[]> toLoadPair = mainArgs.parseReturnUnused(args);
		Map<String, String> toLoad = toLoadPair.left();
		String[] remainingArgs = toLoadPair.right();

		Map<String, AssetLoader> available = getAvailableAssetLoaders();

		if (toLoad.containsKey("loader") && available.containsKey(toLoad.get("loader")))
		{
			AssetLoader loader = available.get(toLoad.get("loader"));
			Pair<Optional<Displayable>, Optional<String>> p = loadDisplayable(loader, remainingArgs);
			runUIOrExit(p.left(), p.right());
		}
		else
		{
			System.err.println(mainArgs.getHelpMessage());
			System.exit(3);
		}
	}

	private static Map<String, AssetLoader> getAvailableAssetLoaders()
	{
		Map<String, AssetLoader> loaders = new HashMap<>();

		loaders.put("cli", new CommandLineAssetLoader());
		loaders.put("gui", new GraphicalAssetLoader());

		return loaders;
	}

	private static void runUIOrExit(Optional<Displayable> userInterface, Optional<String> error)
	{
		if (userInterface.isPresent())
		{
			userInterface.get().run();
		}
		else if (error.isPresent())
		{
			System.err.println(error.get());
			System.exit(1);
		}
		else
		{
			System.err.println();
			System.exit(2);
		}
	}

	private static Pair<Optional<Displayable>, Optional<String>> loadDisplayable(AssetLoader loader, String[] args)
	{
		try
		{
			return new Pair<Optional<Displayable>, Optional<String>>(Optional.of(loader.loadDisplayer(args)), Optional.empty());	
		}
		catch (IOException e)
		{
			return new Pair<Optional<Displayable>, Optional<String>>(Optional.empty(), Optional.of(e.getMessage()));
		}
	}
}
