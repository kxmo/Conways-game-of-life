package core;

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
}
