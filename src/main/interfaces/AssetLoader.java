package main.interfaces;

import java.io.IOException;

import ui.interfaces.Displayable;

public interface AssetLoader
{
	/**
	 * Loads all assets for the required Displayable.
	 * @param cmdLineArgs The command line arguments to the program. May be empty.
	 * @return A displayable with all assets loaded.
	 * @throws IOException Iff the required assets could not 
	 * be loaded. The IOException must have a user friendly
	 * error message describing what went wrong and, if applicable,
	 * how to fix the problem.
	 */
	Displayable loadDisplayer(String[] cmdLineArgs) throws IOException;
}
