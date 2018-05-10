package core.logic;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.ImmutableSet;
import datastructures.Pair;

public class Rules
{
	public static Board applyRulesToBoard(Board board)
	{
		ImmutableSet<Pair<Position, Cell>> positionToCell = cellsToCheck(board).map(p -> new Pair<Position, Cell>(p, nextCellState(board, p)));
		ImmutableSet<Pair<Position, Cell>> positionToAliveCell = positionToCell.filter(pair -> pair.right().equals(Cell.Alive));
		ImmutableSet<Position> aliveCells = positionToAliveCell.map(Pair::left);
		
		return new Board(aliveCells);
	}

	public static Cell nextCellState(Board board, Position pos)
	{
		return nextCellStateRule(board.cellAt(pos), board.aliveNeighbourCount(pos));
	}

	private static Cell nextCellStateRule(Cell c, int neighbourCount)
	{
		if (c.equals(Cell.Alive))
		{
			if (neighbourCount == 2 || neighbourCount == 3)
			{
				return Cell.Alive;
			}
		}
		else if (c.equals(Cell.Dead) && neighbourCount == 3)
		{
			return Cell.Alive;
		}

		return Cell.Dead;
	}

	/**
	 * The cells to check are all cells on the existing board
	 * and all of the neighbours of the alive cells on the board
	 * (to fulfill the "Dead cell 3 neighbours" condition 
	 * @param board
	 * @return
	 */
	private static ImmutableSet<Position> cellsToCheck(Board board)
	{
		ImmutableSet<Position> liveCells = board.aliveCells();
		ImmutableSet<Position> liveCellNeighbours = liveCells.map(board::aliveNeighbours).reduce((a, b) -> a.union(b)).get();

		return liveCells.union(liveCellNeighbours);
	}
}
