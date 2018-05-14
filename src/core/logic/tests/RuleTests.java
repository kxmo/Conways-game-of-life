package core.logic.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import core.logic.Rules;
import core.structures.Board;
import core.structures.Cell;
import core.structures.NeighbourCount;
import core.structures.Position;

public class RuleTests
{
	/*
	 * Conventions:
	 * functionUnderTest_initalState_onCondition_resultingState
	 * 
	 * 'when' and 'then' are used as a DSL for testing.
	 * 'when' is specific to the function under test and is overloaded.
	 */

	private final Board emptyBoard = new Board();
	
	@Test
	public void cellStateRule_alive_lesser2neighbours_dead()
	{
		when (Cell.Alive, NeighbourCount.N0, then(Cell.Dead));
		when (Cell.Alive, NeighbourCount.N1, then(Cell.Dead));
	}
	
	@Test
	public void cellStateRule_alive_2or3neighbours_alive()
	{
		when (Cell.Alive, NeighbourCount.N2, then(Cell.Alive));
		when (Cell.Alive, NeighbourCount.N3, then(Cell.Alive));
	}

	@Test
	public void cellStateRule_alive_greater3neighbours_dead()
	{
		when (Cell.Alive, NeighbourCount.N4, then(Cell.Dead));
		when (Cell.Alive, NeighbourCount.N5, then(Cell.Dead));
		when (Cell.Alive, NeighbourCount.N6, then(Cell.Dead));
		when (Cell.Alive, NeighbourCount.N7, then(Cell.Dead));
		when (Cell.Alive, NeighbourCount.N8, then(Cell.Dead));
	}

	@Test
	public void cellStateRule_dead_lesser3neighbours_dead()
	{
		when (Cell.Dead, NeighbourCount.N0, then(Cell.Dead));
		when (Cell.Dead, NeighbourCount.N1, then(Cell.Dead));
		when (Cell.Dead, NeighbourCount.N2, then(Cell.Dead));
	}
	
	@Test
	public void cellStateRule_dead_greater3neighbours_dead()
	{
		when (Cell.Dead, NeighbourCount.N4, then(Cell.Dead));
		when (Cell.Dead, NeighbourCount.N5, then(Cell.Dead));
		when (Cell.Dead, NeighbourCount.N6, then(Cell.Dead));
		when (Cell.Dead, NeighbourCount.N7, then(Cell.Dead));
		when (Cell.Dead, NeighbourCount.N8, then(Cell.Dead));
	}
	
	@Test
	public void cellStateRule_dead_exactly3neighbours_alive()
	{
		when (Cell.Dead, NeighbourCount.N3, then(Cell.Alive));
	}
	
	
	
	@Test
	public void applyRules_emptyBoard_is_empty()
	{
		next (emptyBoard, is(emptyBoard));
	}

	@Test
	public void applyRules_square_is_unchanged()
	{
		Board square = new Board().addCell(Cell.Alive, new Position(2, 2))
		                          .addCell(Cell.Alive, new Position(3, 2))
		                          .addCell(Cell.Alive, new Position(3, 1))
		                          .addCell(Cell.Alive, new Position(2, 1));

		next (square, is(square));
	}
	
	@Test
	public void applyRules_squareCorner_generates_square()
	{
		Board corner = new Board().addCell(Cell.Alive, new Position(3, 2))
		                          .addCell(Cell.Alive, new Position(4, 2))
		                          .addCell(Cell.Alive, new Position(3, 1));
		Board square = corner.addCell(Cell.Alive, new Position(4, 1));
		next (corner, is(square));
	}
	
	
	private void next(Board input, Board output)
	{
		assertEquals(output, Rules.applyRules(input));
	}

	/**
	 * Test for nextCellStateRule.
	 * @param input The starting cell state.
	 * @param neighbours The number of neighbours input has.
	 * @param output The expected result. Optionally wrapped in 'then'.
	 */
	private void when(Cell input, NeighbourCount neighbours, Cell output)
	{
		assertEquals(output, Rules.nextCellStateRule(input, neighbours));
	}

	
	/*
	 * id but improves readability.
	 * when (Cell.Alive, NeighbourCount.N2, then(Cell.Alive))
	 * vs
	 * when (Cell.Alive, NeighbourCount.N2, Cell.Alive)
	 * It would be easy to mix up the start/end state with the latter
	 * but the former reads left to right.
	 */
	
	private <T> T then(T e)
	{
		return e;
	}
	
	private <T> T is(T e)
	{
		return e;
	}
}
