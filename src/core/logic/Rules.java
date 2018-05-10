package core.logic;

import core.structures.Board;
import core.structures.Cell;
import core.structures.NeighbourCount;
import core.structures.Position;
import datastructures.ImmutableSet;
import datastructures.Pair;

public class Rules
{
	public static Board applyRules(Board board)
	{
		ImmutableSet<Pair<Position, Cell>> positionToCell = cellsToCheck(board).map(p -> new Pair<Position, Cell>(p, nextCellState(board, p)));
		ImmutableSet<Pair<Position, Cell>> positionToAliveCell = positionToCell.filter(pair -> pair.right().equals(Cell.Alive));
		ImmutableSet<Position> aliveCells = positionToAliveCell.map(Pair::left);
		
		return new Board(aliveCells);
	}

	public static Cell nextCellStateRule(Cell c, NeighbourCount count)
	{
		if (c.equals(Cell.Alive))
		{
			if (count == NeighbourCount.N2 || count == NeighbourCount.N3)
			{
				return Cell.Alive;
			}
		}
		else if (c.equals(Cell.Dead) && count == NeighbourCount.N3)
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
		ImmutableSet<Position> liveCellNeighbours = ImmutableSet.fromStream(liveCells.stream().flatMap(p -> board.aliveNeighbours(p).stream()));

		return liveCells.union(liveCellNeighbours);
	}
	
	private static Cell nextCellState(Board board, Position pos)
	{
		return nextCellStateRule(board.cellAt(pos), board.aliveNeighbourCount(pos));
	}
}
