package core.logic.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import core.logic.Rules;
import core.structures.Cell;
import core.structures.NeighbourCount;

public class RuleTests
{
	/*
	 * Conventions:
	 * functionUnderTest_initalState_onCondition_resultingState
	 * 
	 * 'when' and 'then' are used as a DSL for testing.
	 * 'when' is specific to the function under test and is overloaded.
	 */
	
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

	/**
	 * id but improves readability.
	 * when (Cell.Alive, NeighbourCount.N2, then(Cell.Alive))
	 * vs
	 * when (Cell.Alive, NeighbourCount.N2, Cell.Alive)
	 * It would be easy to mix up the start/end state with the latter
	 * but the former reads left to right.
	 * @param e
	 * @return
	 */
	private <T> T then(T e)
	{
		return e;
	}
}