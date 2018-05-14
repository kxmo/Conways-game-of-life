package datastructures;

public class Pair<L, R>
{
	private final L left;
	private final R right;
	
	public Pair(L left, R right)
	{
		this.left = left;
		this.right = right;
	}
	
	public L left()
	{
		return left;
	}
	
	public R right()
	{
		return right;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof Pair)
		{
			Pair<?,?> p = (Pair<?,?>) o;
			isEqual = this.left.equals(p.left) && this.right.equals(p.right);
		}
		
		return isEqual;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}
	
	@Override
	public String toString()
	{
		return String.format("(%s, %s)", left.toString(), right.toString());
	}
}
