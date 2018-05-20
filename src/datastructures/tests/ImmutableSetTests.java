package datastructures.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import core.structures.Position;
import datastructures.ImmutableSet;

public class ImmutableSetTests
{
	/*
	 * Add tests
	 */
	
	@Test
	public void add_non_assignment_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<>();
		ImmutableSet<Integer> next = initial;
		next.add(1);
		assertEquals(initial, next);
		assertEquals(initial, new ImmutableSet<>());
	}
	
	@Test
	public void add_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<>();
		ImmutableSet<Integer> next = initial.add(2);
		
		assertNotEquals(initial, next);
		assertEquals(new ImmutableSet<>(), initial);
		assertEquals(new ImmutableSet<>().add(2), next);
	}
	
	@Test
	public void add_item_present_single_element()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		ImmutableSet<Integer> next = initial.add(1);
		
		assertEquals(1, next.size());
		assertEquals(new ImmutableSet<Integer>().add(1), initial);
		assertEquals(initial, next);
	}
	
	
	/*
	 * Remove tests 
	 */
	
	@Test
	public void remove_returns_equal_set_when_element_not_present()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(3);
		ImmutableSet<Integer> expected = new ImmutableSet<Integer>().add(1).add(3);
		
		assertEquals(expected, initial.remove(2));
	}
	
	@Test
	public void remove_non_assignment_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		ImmutableSet<Integer> next = new ImmutableSet<Integer>().add(1);
		next.remove(1);
		assertEquals(initial, next);
	}
	
	@Test
	public void remove_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		ImmutableSet<Integer> next = initial.add(2).remove(1);
		
		assertNotEquals(initial, next);
		assertEquals(new ImmutableSet<>().add(1), initial);
		assertEquals(new ImmutableSet<>().add(2), next);
	}
	
	@Test
	public void remove_item_not_present_set_unchanged()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		ImmutableSet<Integer> next = initial.remove(2);
		
		assertEquals(1, next.size());
		assertEquals(new ImmutableSet<Integer>().add(1), initial);
		assertEquals(initial, next);
	}
	
	/*
	 * Contains tests
	 */
	
	@Test
	public void contains_non_element_false()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		assertFalse(initial.contains(2));
	}
	
	@Test
	public void contains_non_element_empty_false()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>();
		assertFalse(initial.contains(1));
	}
	
	@Test
	public void contains_only_element_true()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		assertTrue(initial.contains(1));
	}
	
	@Test
	public void contains_first_element_true()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(2);
		assertTrue(initial.contains(1));
	}
	
	@Test
	public void contains_last_element_true()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(2);
		assertTrue(initial.contains(2));
	}
	
	@Test
	public void contains_middle_element_true()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(2).add(3);
		assertTrue(initial.contains(2));
	}
	
	@Test
	public void contains_removed_element_false()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(2).remove(2).add(3);
		assertFalse(initial.contains(2));
	}
	
	
	/*
	 * Union tests
	 */
	
	@Test
	public void union_non_assignment_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(3);
		ImmutableSet<Integer> next = new ImmutableSet<Integer>().add(2);
		next.union(initial);
		assertEquals(new ImmutableSet<>().add(2), next);
	}
	
	@Test
	public void union_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(3);
		ImmutableSet<Integer> next = new ImmutableSet<Integer>().add(2);

		next = next.union(initial);
		
		assertNotEquals(initial, next);
		assertEquals(new ImmutableSet<>().add(1).add(3), initial);
		assertEquals(new ImmutableSet<>().add(1).add(2).add(3), next);
	}
	
	@Test
	public void union_with_empty_equals_initial()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1);
		ImmutableSet<Integer> next = new ImmutableSet<>();
		
		assertNotEquals(initial, next);
		assertEquals(initial.union(next), initial);
	}
	
	@Test
	public void union_contains_all_elements_from_both_sets()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<Integer>().add(1).add(3);
		ImmutableSet<Integer> next = new ImmutableSet<Integer>().add(2);
		
		ImmutableSet<Integer> union = initial.union(next);
		
		assertEquals(initial.size() + next.size(), union.size());
		assertEquals(new ImmutableSet<>().add(1).add(2).add(3), union);
	}
	
	
	/*
	 * Clone tests
	 */
	
	@Test
	public void clone_non_assignment_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<>();
		ImmutableSet<Integer> next = initial;
		next.clone();
		assertEquals(initial, next);
		assertEquals(initial, new ImmutableSet<>());
	}
	
	@Test
	public void clone_does_not_mutate_set()
	{
		ImmutableSet<Integer> initial = new ImmutableSet<>();
		ImmutableSet<Integer> next = initial.clone().add(1);
		
		assertNotEquals(initial, next);
		assertEquals(initial, new ImmutableSet<>());
		assertEquals(next, new ImmutableSet<Integer>().add(1));
	}
	
	
	/*
	 * Stream tests
	 */
	
	@Test
	public void stream_only_modifies_new_set()
	{
		ImmutableSet<Integer> set = new ImmutableSet<Integer>().add(1)
				.add(2)
				.add(3);
		List<Integer> list = set.stream().map(i -> i + 1).collect(Collectors.toList());
		List<Integer> expectedList = new ArrayList<>(Arrays.asList(2,3,4));
		
		assertEquals(new ImmutableSet<>().add(1).add(2).add(3), set);
		assertEquals(expectedList, list);
	}

	
	
	/*
	 * Equality tests
	 */
	
	private final class MutableObject
	{
		public int x;
		
		public MutableObject(int x)
		{
			this.x = x;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ImmutableSetTests.this.hashCode();
			result = prime * result + x;
			return result;
		}

		@Override
		public boolean equals(Object o)
		{
			boolean isEqual = false;
			
			if (o instanceof MutableObject)
			{
				MutableObject m = (MutableObject) o;
				isEqual = this.x == m.x;
			}
			
			return isEqual;
		}
	}
	
	@Test
	public void equals_empty_sets_equal()
	{
		assertEquals(new ImmutableSet<>(), new ImmutableSet<>());
	}
	
	@Test
	public void equals_empty_nonEmpty_unequal()
	{
		assertNotEquals(new ImmutableSet<>().add(1), new ImmutableSet<>());
	}
	
	@Test
	public void equals_nonEmpty_nonEmpty_equal()
	{
		assertEquals(new ImmutableSet<>().add(1), new ImmutableSet<>().add(1));
	}
	
	@Test
	public void equals_with_equal_object_elements_are_equal()
	{
		Position p1 = new Position(1,3);
		Position p2 = new Position(1,3);
		
		ImmutableSet<Position> set1 = new ImmutableSet<Position>().add(p1);
		ImmutableSet<Position> set2 = new ImmutableSet<Position>().add(p2);
		
		assertEquals(p1, p2);
		assertEquals(set1, set2);
	}
	
	/**
	 * This test checks that changing inner elements of 
	 * two sets (that contain == elements) changes both.
	 * This is an enforcement of the interface and known
	 * behaviour rather than the ideal behaviour.
	 * 
	 * For reasons behind this design decision see
	 * the ImmutableSet class header.
	 */
	@Test
	public void equals_innerElements_change_equal()
	{
		MutableObject m = new MutableObject(0);
		
		ImmutableSet<MutableObject> set1 = new ImmutableSet<MutableObject>().add(m);
		ImmutableSet<MutableObject> set2 = new ImmutableSet<MutableObject>().add(m);
		
		assertEquals(new ImmutableSet<>().add(new MutableObject(0)), set1);
		assertEquals(new ImmutableSet<>().add(new MutableObject(0)), set2);
		assertEquals(set1, set2);

		m.x = 1;

		assertEquals(new ImmutableSet<>().add(new MutableObject(1)), set1);
		assertEquals(new ImmutableSet<>().add(new MutableObject(1)), set2);
		assertEquals(set1, set2);
	}
}
