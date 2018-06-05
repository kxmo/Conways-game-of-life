package ui.terminal;

import java.util.List;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.Pair;
import ui.interfaces.CellDisplayer;

public class CommandLine extends CellDisplayer<String>
{
	public CommandLine(String alive, String dead)
	{
		super(alive, dead);
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
		// TODO: Implement cmdline parsing in CmdAssetLoader and reference results here - add cmd args to all loaders?
	}
}
