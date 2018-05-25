package ui.terminal;

import java.util.List;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.Pair;

public class CommandLine
{
	public static void displayState(Board board)
	{
		for (List<Pair<Cell, Position>> row : board.rows())
		{
			for (Pair<Cell, Position> p : row)
			{
				System.out.print(cellToString(p.left()));
			}
			System.out.println();
		}
	}
	
	private static String cellToString(Cell c)
	{
		switch(c)
		{
			case Alive:
				return "*";
			case Dead:
				return " ";
			default:
				return String.format("Unknown cell: %s", c.toString());
		}
	}
}
