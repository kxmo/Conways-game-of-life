package ui.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.swing.JPanel;

import core.structures.Board;
import core.structures.Cell;
import core.structures.Position;
import datastructures.Pair;

public class CellPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// Java does not support multiple inheritance, and output
	// comes from the abstract class CellDisplayer.
	private final Function<Cell, Image> output;
	private Board board;
	
	public CellPanel(Function<Cell, Image> output)
	{
		this.output = output;
	}
	
	public void setBoard(Board b)
	{
		this.board = b;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		if (this.board == null)
		{
			return;
		}

		final List<List<Pair<Cell, Position>>> rows = this.board.rows();
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
				final Image cell = this.output.apply(rows.get(row).get(col).left());
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
