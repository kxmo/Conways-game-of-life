package ui.gui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;

import core.structures.Board;
import ui.GameManager;
import ui.interfaces.CellDisplayer;

public class Graphical extends CellDisplayer<Image>
{
	private final GameManager game;
	private final JFrame window;
	private static final String windowTitle = "Conway's game of life";
	
	private final CellPanel cells;

	public Graphical(Image alive, Image dead, GameManager game)
	{
		super(alive, dead);

		this.game = game;
		this.game.addObserver(this);
		
		cells = new CellPanel(this::output);
		
		window = new JFrame(windowTitle);
		setupFrame();
	}
	
	private void setupFrame()
	{
		window.setVisible(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setSize(500, 500);
		window.setPreferredSize(new Dimension(500, 500));

		window.add(cells);
		
		window.setVisible(true);
	}

	@Override
	public void update(Board board)
	{
		cells.setBoard(board);
		window.repaint();
	}

	@Override
	public void run()
	{
		game.start();
		// This is an interactive interface and will run only when the user selects the appropriate options.
		// TODO: Implement interactive GUI for users
	}
}
