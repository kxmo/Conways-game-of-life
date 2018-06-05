package ui.interfaces;

import core.structures.Board;
import main.interfaces.GenericObserver;

/**
 * Display (graphically or otherwise) an iteration of the board.
 */
public interface Displayable extends GenericObserver<Board>
{
	/**
	 * Start the game of life.
	 */
	public void run();
}
