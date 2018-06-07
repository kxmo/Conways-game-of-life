package ui.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.Pair;
import ui.GameManager;
import ui.interfaces.CellDisplayer;

public class Graphical extends CellDisplayer<Image>
{
	private final GameManager game;
	private final JFrame window;
	private static final String windowTitle = "Conway's game of life";

	private Board board;

	public Graphical(Image alive, Image dead, GameManager game)
	{
		super(alive, dead);

		this.game = game;
		this.game.addObserver(this);
		
		window = new JFrame(windowTitle);
		window.setVisible(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setSize(500, 500);
		window.setPreferredSize(new Dimension(500, 500));

		window.add(new PaintCell());
		
		window.setVisible(true);
	}

	@Override
	public void update(Board board)
	{
		this.board = board;
		window.repaint();
	}

	private class PaintCell extends JPanel
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g)
		{
			if (board == null)
			{
				return;
			}
			
			final List<List<Pair<Cell, Position>>> rows = board.rows();
			final int rowItemCount = rows.size();
			
			if (rowItemCount == 0)
			{
				return;
			}
			
			final int colItemCount = rows.get(0).size();
			final int canvasWidth = this.getWidth();
			final int canvasHeight = this.getHeight();
			
			final Double cellWidth = new Double((double) canvasWidth / (double) colItemCount);
			final Double cellHeight = new Double((double) canvasHeight / (double) rowItemCount);

			for (int row = 0; row < rowItemCount; row++)
			{
				Optional<Double> rowY = Optional.empty();
								
				for (int col = 0; col < colItemCount; col++)
				{
					final Image cell = output(rows.get(row).get(col).left());
					Double colX = col * cellWidth;
					rowY = Optional.of(row * cellHeight);
					
					g.drawImage(cell, colX.intValue(), rowY.get().intValue(), cellWidth.intValue(), cellHeight.intValue(), null);
					
					// Rounding may cause gaps between the image and the grid, we should undershoot rather than overshoot
					colX = colX.doubleValue() - 1;
					rowY = Optional.of(rowY.get().doubleValue() - 1);
					
					g.drawLine(colX.intValue(), 0, colX.intValue(), canvasHeight);
				}
				
				if (rowY.isPresent())
				{
					g.drawLine(0, rowY.get().intValue(), canvasWidth, rowY.get().intValue());
				}
				
			}
		}
	}

	@Override
	public void run()
	{
		game.start(500);
		// This is an interactive interface and will run only when the user selects the appropriate options.
		// TODO: Implement interactive GUI for users
	}
}
