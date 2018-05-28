package main;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import ui.terminal.CommandLine;

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
		
		new CommandLine("c"," ").display(board);
	}
}
