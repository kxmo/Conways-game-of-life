package main;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import ui.gui.Graphical;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Board board = new Board();
		

		board = board.addCell(Cell.Alive, new Position(3, 0));		
		board = board.addCell(Cell.Alive, new Position(2, 3));
		board = board.addCell(Cell.Alive, new Position(4, 3));
		board = board.addCell(Cell.Alive, new Position(0, 1));
		board = board.addCell(Cell.Alive, new Position(3, 5));
		board = board.addCell(Cell.Alive, new Position(2, 1));
		board = board.addCell(Cell.Alive, new Position(1, 4));
		board = board.addCell(Cell.Alive, new Position(1, 2));

		Image alive = ImageIO.read(new File("src/ui/gui/assets/alive.png"));
		Image dead = ImageIO.read(new File("src/ui/gui/assets/dead.png"));

		new Graphical(alive, dead).display(board);
	}
}
