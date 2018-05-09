package core;

import java.util.Set;

import board.Board;
import util.SetUtil;

public class Rules
{
	public Cell nextCellState(Board board, Position pos)
	{
		return nextCellStateRule(board.cellAt(pos), board.aliveNeighbourCount(pos));
	}
	
	public Cell nextCellStateRule(Cell c, int neighbourCount)
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
	private Board cellsToCheck(Board board)
	{
		Set<Position> liveCells = board.aliveCells();
		Set<Position> liveCellNeighbours = liveCells.stream().map(board::aliveNeighbours).reduce(SetUtil::union).get();
		
		return new Board(SetUtil.union(liveCells, liveCellNeighbours));
	}
}
