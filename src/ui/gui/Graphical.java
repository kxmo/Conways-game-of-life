package ui.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
		update(game.getCurrentData()); // Display the board before starting the game
	}
	
	private void setupFrame()
	{
		window.setPreferredSize(new Dimension(500, 500));
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel options = new OptionsPanel(game);
		cells.setPreferredSize(new Dimension(
				(int) window.getContentPane().getPreferredSize().getWidth(),
				(int) (window.getContentPane().getPreferredSize().getHeight() - options.getPreferredSize().getHeight())));

		window.setLayout(new BorderLayout());
		window.add(cells, BorderLayout.CENTER);
		window.add(options, BorderLayout.SOUTH);
		
        window.pack();
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
		// This is an interactive interface and will run only when the user selects the appropriate options.
	}
}
