package core.structures;

import java.util.Optional;

/**
 * The number of alive neighbours a cell has.
 */
public enum NeighbourCount
{
	N0,
	N1,
	N2,
	N3,
	N4,
	N5,
	N6,
	N7,
	N8;
	
	public static Optional<NeighbourCount> neighbourCountFromInt(int count)
	{
		// It would be nice to be able to check that the usage is valid at compile time.
		// This function assumes neighbours go from 0 to n and that Neighbours.values() does the same.
		if (count < 0 || count >= NeighbourCount.values().length)
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(NeighbourCount.values()[count]);
		}
	}
}


