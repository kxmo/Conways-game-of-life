package board;

import java.util.Set;

import core.Cell;
import core.Position;
import util.SetUtil;

/**
 * The board keeps track of cells in the universe.
 * All boards are immutable and all changes return a new board
 * with the applicable changes.
 * 
 * Whether or not the board wraps is currently undefined.
 * 
 * If any arguments are null to any function the behaviour is undefined.
 */
public class Board
{
	// The set of alive cells. Equivalent to a sparse matrix.
	private final Set<Position> cells;
	
	public Board()
	{
		this.cells = SetUtil.newSet();
	}
	
	private Board(Set<Position> cells)
	{
		this.cells = SetUtil.copy(cells);
	}
	
	/**
	 * Add a cell at the provided position to the board.
	 * @param c The cell to add.
	 * @param p The position to add it to.
	 * @return The board is changed iff an alive cell becomes
	 * dead or a dead cell becomes alive. Otherwise the board
	 * remains the same as the old board.
	 */
	public Board addCell(Cell c, Position p)
	{
		if (c == null || p == null)
		{
			return new Board(cells);
		}
		
		if (cells.contains(p))
		{
			if (c.equals(Cell.Dead))
			{
				return new Board(SetUtil.remove(cells, p));
			}
			else
			{
				// The cell is alive and already on the board
			}
		}
		else if (c.equals(Cell.Alive))
		{
			return new Board(SetUtil.add(cells, p));
		}
		else
		{
			// The cell is not on the board and is dead
		}
		
		return new Board(cells); // Make no change
	}
	
	/**
	 * Get the status of a cell at a position.
	 * @param p The position of the cell.
	 * @return Cell.Alive iff the cell is alive, Cell.Dead iff the cell is dead.
	 */
	public Cell cellAt(Position p)
	{
		if (p == null)
		{
			throw new IllegalArgumentException("No arguments may be null");
		}
		
		if (cells.contains(p))
		{
			return Cell.Alive;
		}
		else
		{
			return Cell.Dead;
		}
	}
}
