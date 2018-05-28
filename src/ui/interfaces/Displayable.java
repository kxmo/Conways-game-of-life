package ui.interfaces;

import core.structures.Board;

/**
 * Display (graphically or otherwise) an iteration of the board.
 */
public interface Displayable
{
	/**
	 * @param board The current version of the board to display.
	 */
	public void display(Board board);
}
