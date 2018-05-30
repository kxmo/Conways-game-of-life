package ui.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.interfaces.AssetLoader;
import ui.interfaces.Displayable;

public class GraphicalAssetLoader implements AssetLoader
{
	@Override
	public Displayable loadDisplayer() throws IOException
	{
		Image alive = ImageIO.read(new File("src/ui/gui/assets/alive.png"));
		Image dead = ImageIO.read(new File("src/ui/gui/assets/dead.png"));

		return new Graphical(alive, dead);
	}
}
