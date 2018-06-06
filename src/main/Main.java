package main;

import java.io.IOException;
import java.util.Optional;

import datastructures.Pair;
import main.interfaces.AssetLoader;
import ui.interfaces.Displayable;
import ui.terminal.CommandLineAssetLoader;

public class Main
{
	public static void main(String[] args)
	{
		Pair<Optional<Displayable>, Optional<String>> p = loadDisplayable(new CommandLineAssetLoader(), args);
		
		Optional<Displayable> d = p.left();
		Optional<String> e = p.right();
		
		if (d.isPresent())
		{
			d.get().run();
		}
		else if (e.isPresent())
		{
			System.err.println(e.get());
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
