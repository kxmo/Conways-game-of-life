package core.structures;

import java.util.Arrays;

import datastructures.ImmutableSet;

/**
 * A valid position in the universe.
 * Values may be negative.
 */
public class Position
{
	private final int x;
	private final int y;
	
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public boolean isNeighbour(Position p)
	{
		return getNeighbours().contains(p);
	}
	
	public ImmutableSet<Position> getNeighbours()
	{
		return new ImmutableSet<Position>(Arrays.asList(
				new Position(x - 1, y + 1),
				new Position(x, y + 1),
				new Position(x + 1, y + 1),
				new Position(x - 1, y),
				new Position(x + 1, y),
				new Position(x - 1, y - 1),
				new Position(x, y - 1),
				new Position(x + 1, y - 1)));
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof Position)
		{
			Position p = (Position) o;
			isEqual = this.x == p.x && this.y == p.y;
		}
		
		return isEqual;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public String toString()
	{
		return String.format("(%d, %d)", x, y);
	}
}
