package ui.terminal;

import java.util.List;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.Pair;
import ui.GameManager;
import ui.interfaces.CellDisplayer;

public class CommandLine extends CellDisplayer<String>
{
	private final GameManager game;
		
	public CommandLine(String alive, String dead, GameManager game)
	{
		super(alive, dead);
		this.game = game;
		this.game.addObserver(this);
	}

	@Override
	public void update(Board board)
	{
		for (List<Pair<Cell, Position>> row : board.rows())
		{
			for (Pair<Cell, Position> p : row)
			{
				System.out.print(output(p.left()));
			}
			System.out.println();
		}
	}

	@Override
	public void run()
	{
		game.start();
	}
}
