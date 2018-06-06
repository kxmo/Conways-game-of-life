package ui.terminal;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;

import java.util.Optional;

import main.interfaces.AssetLoader;
import parsing.terminal.Parser;
import ui.GameManager;
import ui.interfaces.Displayable;

public class CommandLineAssetLoader implements AssetLoader
{
	@Override
	public Displayable loadDisplayer(String[] cmdLineArgs) throws IOException
	{
		Parser options = new Parser();
		options.addOption("g", "generations", "The total number of generations to run", true, true);
		options.addOption("a", "alive", "The character that represents an alive cell", true, true);
		options.addOption("d", "dead", "The character that represents a dead cell", true, true);
		Optional<Map<String, String>> opt = options.parse(cmdLineArgs);

		if (opt.isPresent())
		{
			int generations = 1;
			String alive = "*";
			String dead = " ";

			for (Entry<String, String> e : opt.get().entrySet())
			{
				String key = e.getKey();
				String val = e.getValue();

				switch (key)
				{
					case "generations":
						Optional<Integer> g = Parser.parseInt(val);
						if (g.isPresent() && g.get() >= 0)
						{
							generations = g.get();
						}
						else
						{
							String message = String.format("%s\n\n%s", "Could not parse generations - must be an integer >= 0", options.getUsage());
							throw new IOException(message);
						}
						break;
					case "alive":
						alive = val;
						break;
					case "dead":
						dead = val;
						break;
				}
			}
			Board board = new Board();

			board = board.addCell(Cell.Alive, new Position(3, 0));		
			board = board.addCell(Cell.Alive, new Position(2, 3));
			board = board.addCell(Cell.Alive, new Position(4, 3));
			board = board.addCell(Cell.Alive, new Position(0, 1));
			board = board.addCell(Cell.Alive, new Position(3, 5));
			board = board.addCell(Cell.Alive, new Position(2, 1));
			board = board.addCell(Cell.Alive, new Position(1, 4));
			board = board.addCell(Cell.Alive, new Position(1, 2));

			GameManager game = new GameManager(board, generations);

			return new CommandLine(alive, dead, game);
		}
		else
		{
			String message = String.format("%s\n\n%s", options.getHelpMessage().get(), options.getUsage());
			throw new IOException(message);
		}
	}
}
