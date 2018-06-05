package main;

import java.io.IOException;
import java.util.Optional;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.Pair;
import main.interfaces.AssetLoader;
import ui.gui.GraphicalAssetLoader;
import ui.interfaces.Displayable;

public class Main
{
	public static void main(String[] args)
	{
		Board board = new Board();
		

		board = board.addCell(Cell.Alive, new Position(3, 0));		
		board = board.addCell(Cell.Alive, new Position(2, 3));
		board = board.addCell(Cell.Alive, new Position(4, 3));
		board = board.addCell(Cell.Alive, new Position(0, 1));
		board = board.addCell(Cell.Alive, new Position(3, 5));
		board = board.addCell(Cell.Alive, new Position(2, 1));
		board = board.addCell(Cell.Alive, new Position(1, 4));
		board = board.addCell(Cell.Alive, new Position(1, 2));

		Pair<Optional<Displayable>, Optional<String>> p = loadDisplayable(new GraphicalAssetLoader());
		
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
	
	private static Pair<Optional<Displayable>, Optional<String>> loadDisplayable(AssetLoader loader)
	{
		try
		{
			return new Pair<Optional<Displayable>, Optional<String>>(Optional.of(loader.loadDisplayer()), Optional.empty());	
		}
		catch (IOException e)
		{
			return new Pair<Optional<Displayable>, Optional<String>>(Optional.empty(), Optional.of(e.getMessage()));
		}
	}
}
