package ui;

import java.io.IOException;

import main.interfaces.AssetLoader;
import ui.interfaces.Displayable;

/**
 * An AssetLoader that does not load any assets, but instead
 * returns the provided Displayable.
 * This should be used in place of one's own implementation
 * of this class if they intend to load no assets. This loader
 * can also be used if assets are generated in a way that cannot
 * fail.
 */
public class EmptyLoader implements AssetLoader
{
	private final Displayable d;
	
	public EmptyLoader(Displayable d)
	{
		this.d = d;
	}
	
	@Override
	public Displayable loadDisplayer(String[] cmdLineArgs) throws IOException
	{
		return d;
	}
}
