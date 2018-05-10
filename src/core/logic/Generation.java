package core.logic;

import core.structures.Board;

/**
 * A high level interface for interacting with the board and ruleset.
 */
public class Generation
{
	/**
	 * Run a number of generations on the existing board.
	 * 
	 * @param count
	 * @return The provided board iff count < 0, a new board otherwise.
	 */
	public static Board runGenerations(Board board, int count)
	{
		if (count < 0)
		{
			return board;
		}
		
		for (int i = 1; i <= count; i++)
		{
			board = runGeneration(board);
		}
		
		return board;
	}
	
	public static Board runGeneration(Board b)
	{
		return Rules.applyRulesToBoard(b);
	}
}
