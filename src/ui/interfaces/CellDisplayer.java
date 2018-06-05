package ui.interfaces;

import core.structures.Cell;

/**
 * The base class for UIs relating to the displaying of the world.
 * The primary attribute of this class is the presence of output
 * @param <T> The type of output given by the extending class.
 * For example a GUI application may use Image, and a command line
 * application may use String or Character.
 */
public abstract class CellDisplayer<T> implements Displayable
{
	private final T alive;
	private final T dead;

	public CellDisplayer(T alive, T dead)
	{
		this.alive = alive;
		this.dead = dead;
	}
	
	/**
	 * Convert a Cell c into one of the provided
	 * values in the constructor.
	 * This centralises the switch statement on Cell
	 * because it cannot be implemented into Cell directly.
	 * @param c The cell to switch on.
	 * @throws IllegalArgumentException iff the switch statement
	 * is incomplete. In that case the switch statement is to be updated.
	 * Note that this may be a backwards incompatible change.
	 */
	public T output(Cell c)
	{
		switch(c)
		{
			case Alive:
				return alive;
			case Dead:
				return dead;
			default:
				throw new IllegalArgumentException(String.format("Unknown cell: %s", c.toString()));
		}
	}
}
