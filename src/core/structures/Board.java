package core.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import datastructures.ImmutableSet;
import datastructures.Pair;

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
	private final ImmutableSet<Position> cells;
	
	public Board()
	{
		this.cells = new ImmutableSet<Position>();
	}
	
	/**
	 * @param cells The position of alive cells.
	 */
	public Board(ImmutableSet<Position> cells)
	{
		this.cells = cells.clone();
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
				return new Board(cells.remove(p));
			}
			else
			{
				// The cell is alive and already on the board
			}
		}
		else if (c.equals(Cell.Alive))
		{
			return new Board(cells.add(p));
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
	
	public ImmutableSet<Position> aliveCells()
	{
		return cells;
	}

	public NeighbourCount aliveNeighbourCount(Position p)
	{
		int aliveNeighbours = aliveNeighbours(p).size();
		Optional<NeighbourCount> neighbourCount = NeighbourCount.neighbourCountFromInt(aliveNeighbours);
		
		if (neighbourCount.isPresent())
		{
			return neighbourCount.get();
		}
		else // This should never happen if the board is implemented correctly.
		{
			throw new IllegalStateException(String.format("The board is in an invalid state. Requested aliveNeighbourCount of '%s' on board '%s'", p, cells));
		}
	}
	
	public ImmutableSet<Position> aliveNeighbours(Position p)
	{
		return cells.filter(p::isNeighbour);
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof Board)
		{
			Board board = (Board) o;
			isEqual = this.cells.equals(board.cells);
		}
		
		return isEqual;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cells == null) ? 0 : cells.hashCode());
		return result;
	}
	
	@Override
	public String toString()
	{
		return this.cells.toString();
	}

	public List<List<Pair<Cell, Position>>> rows()
	{
		List<List<Pair<Cell, Position>>> rows = new ArrayList<>();
		
		if (this.cells.isEmpty())
		{
			return rows;
		}
		
		SortedSet<Pair<Cell, Position>> allCells = getAllCells();
		
		int currentY = allCells.first().right().getY();
		List<Pair<Cell, Position>> currentRow = new ArrayList<>();
		
		for (Pair<Cell, Position> p : allCells)
		{
			int positionY = p.right().getY();
			
			if (positionY < currentY) // We're working our way top left to bottom right, so lesser Y is later
			{
				rows.add(currentRow);
				currentY = positionY;
				currentRow = new ArrayList<>();
			}
			
			currentRow.add(p);
		}
		
		rows.add(currentRow);
		
		return rows;
	}
	
	private SortedSet<Pair<Cell, Position>> getAllCells()
	{
		if (this.cells.isEmpty())
		{
			return Collections.emptySortedSet();
		}
		
		// The position comparator when within a Pair
		Comparator<Pair<?, Position>> positionPairComparator = (a,b) -> a.right().compare(a.right(), b.right());
		
		SortedSet<Pair<Cell, Position>> allCells = new TreeSet<>(positionPairComparator);
		
		for (Position p : cells.toSet())
		{
			allCells.add(new Pair<>(Cell.Alive, p));
		}
		
		int minX = this.cells.stream().mapToInt(Position::getX).min().getAsInt();
		int minY = this.cells.stream().mapToInt(Position::getY).min().getAsInt();
		
		int maxX = this.cells.stream().mapToInt(Position::getX).max().getAsInt();
		int maxY = this.cells.stream().mapToInt(Position::getY).max().getAsInt();
		
		for (int x = minX; x <= maxX; x++)
		{
			for (int y = minY; y <= maxY; y++)
			{
				// The alive cells are already present, this is a set, and the comparator is based on
				// Position so we don't need to worry about overwriting the alive cells with dead ones
				allCells.add(new Pair<Cell, Position>(Cell.Dead, new Position(x, y)));
			}
		}
		
		return allCells;
	}
}
