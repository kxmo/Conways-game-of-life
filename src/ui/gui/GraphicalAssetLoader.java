package ui.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import main.interfaces.AssetLoader;
import parsing.terminal.Parser;
import ui.GameManager;
import ui.interfaces.Displayable;

public class GraphicalAssetLoader implements AssetLoader
{
	@Override
	public Displayable loadDisplayer(String[] cmdLineArgs) throws IOException
	{
		Parser options = new Parser();
		options.addOption("a", "alive", "The image that represents an alive cell", true, false);
		options.addOption("d", "dead", "The image that represents a dead cell", true, false);
		Optional<Map<String, String>> opt = options.parse(cmdLineArgs);

		final Image aliveImage;
		final Image deadImage;

		if (opt.isPresent() && opt.get().containsKey("alive"))
		{
			aliveImage = ImageIO.read(new File(opt.get().get("alive")));
		}
		else
		{
			aliveImage = ImageIO.read(new File("src/ui/gui/assets/alive.png"));
		}

		if (opt.isPresent() && opt.get().containsKey("dead"))
		{
			deadImage = ImageIO.read(new File(opt.get().get("dead")));
		}
		else
		{
			deadImage = ImageIO.read(new File("src/ui/gui/assets/dead.png"));
		}

		
		Board board = new Board();

		board = board.addCell(Cell.Alive, new Position(3, 0));		
		board = board.addCell(Cell.Alive, new Position(2, 3));
		board = board.addCell(Cell.Alive, new Position(4, 3));
		board = board.addCell(Cell.Alive, new Position(0, 1));
		board = board.addCell(Cell.Alive, new Position(3, 5));
		board = board.addCell(Cell.Alive, new Position(2, 1));
		board = board.addCell(Cell.Alive, new Position(1, 4));
		board = board.addCell(Cell.Alive, new Position(1, 2));
		
		
		return new Graphical(aliveImage, deadImage, new GameManager(board));
	}
}
