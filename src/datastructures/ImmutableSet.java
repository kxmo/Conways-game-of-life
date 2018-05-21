package datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create an immutable set.
 * 
 * Note that the set itself is immutable, and not necessarily
 * the elements within the set.
 * That is, when you add a mutable element to the set
 * the set does not (and cannot) prevent that element 
 * from being mutated.
 * Importantly, the set also does not copy elements between
 * instances of the set because the elements are assumed to be
 * immutable.
 * @param <T> The type of the elements within the set
 */
public class ImmutableSet<T> implements Cloneable
{
	/*
	 * Lazy design:
	 * 
	 * The implementation of this set is lazy. This design decision
	 * makes the immutable portion of the set more efficient than it
	 * otherwise would be. By executing changes to the set lazily we
	 * can avoid expensive copying of the set for every operation.
	 * 
	 * The semantic usage of the set is identical from the callers
	 * point of view, however. When the caller requires information
	 * about the set (toString, equals, contains etc) the correct
	 * answer must be returned.
	 */

	/*
	 * Lazy implementation:
	 * 
	 * Places where a fully lazy implementation is more difficult than it's worth:
	 * - hashCode
	 * - toString
	 * - equals
	 * - contains
	 * - union
	 * - size
	 * 
	 * Streams:
	 * Streams *need* to be synced before the stream call because callers
	 * can collect the stream into another structure without going through
	 * us so all of our changes need to be present within the stream.
	 */

	/*
	 * Lazy implementation details:
	 * 
	 * All sets have a unique setNum, populated from the static nextSetNum.
	 * The range of setNum is 1 to MAX_INTEGER. TODO: When nextSetNum reaches
	 * MAX_INTEGER the sets are consolidated and nextSetNum wraps to 1. 
	 * 
	 * The existing set elements are kept in `elements'. elements is unique to this.
	 * 
	 * There is ONE list of changes across all sets, segmented by setNum.
	 * changes is TREATED as static, but is not static because it references T. 
	 * The list contains a mapping from setNum to the element and change 
	 * to be made (a LazyAction). For more information on why this is valid
	 * see storeLazyAction().
	 * 
	 * Changes are, in general, not made until they need to be.
	 * This is not true in a few places where the implementation is vastly simpler
	 * (see the list in Lazy Implementation).
	 */

	
	

	/**
	 * A lazy action is an action that we store, to possibly be executed later.
	 * 
	 * The affecting action must mutate the internal set `elements' idempotently.
	 * The actions associated with each LazyAction may be called immediately,
	 * or may never be called. They are guaranteed to be called either 0 or 1 times
	 * for each time that they are added to the collection of changes to be made.
	 */
	private enum LazyAction
	{
		Add, Remove;
	}
	
	
	private class Node<E> implements Iterable<E>
	{
		private final Optional<E> element;
		private final Optional<LazyAction> action;
		private final Optional<Node<E>> parent;
		private final Set<ImmutableSet<T>> sets;
		
		public Node()
		{
			this.element = Optional.empty();
			this.parent = Optional.empty();
			this.action = Optional.empty();
			this.sets = new CopyOnWriteArraySet<>();
		}
		
		public Node(E element, LazyAction action, Node<E> parent)
		{
			this.element = Optional.of(element);
			this.parent = Optional.of(parent);
			this.action = Optional.of(action);
			this.sets = new CopyOnWriteArraySet<>();
		}
		
		public Node(Node<E> s)
		{
			this.element = bind(s.element);
			this.parent = bind(s.parent);
			this.action = bind(s.action);
			this.sets = s.sets;
		}
		
		private <R> Optional<R> bind(Optional<R> m)
		{
			if (m.isPresent())
			{
				return Optional.of(m.get());
			}
			
			return Optional.empty();
		}

		@Override
		public String toString()
		{
			return String.format("%s: %s", action.toString(), element.toString());
		}
		
		public void addSet(ImmutableSet<T> set)
		{
			this.sets.add(set);
		}

		@Override
		public Iterator<E> iterator()
		{
			return new NodeIterator<E>(this);
		}
		
		private final class NodeIterator<R> implements Iterator<R>
		{
			private Node<R> current;
			
			public NodeIterator(Node<R> node)
			{
				this.current = node;
			}
			
			@Override
			public boolean hasNext()
			{
				return current.parent.isPresent();
			}

			@Override
			public R next()
			{
				if (!hasNext())
				{
					throw new NoSuchElementException();
				}
				
				R element = current.element.get();
				
				this.current = this.current.parent.get();
				
				return element;
			}
		}
	}
		
	private final Node<T> empty = new Node<>();
	private Node<T> startingNode;
	
	/**
	 * Create an empty immutable set
	 */
	public ImmutableSet()
	{
		this.startingNode = setStartingNode(empty);
	}
	
	public ImmutableSet(ImmutableSet<T>.Node<T> startingNode2)
	{
		this.startingNode = new Node<T>(startingNode2);
	}

	public ImmutableSet(Collection<T> items)
	{
		ImmutableSet<T> set = new ImmutableSet<>();
		
		for (T item : items)
		{
			set = newSetWithNode(LazyAction.Add, item);
		}
		
		this.startingNode = set.startingNode;
	}

	
	private <E> Node<E> setStartingNode(Node<E> node)
	{
		node.addSet(this);
		return node;
	}


	@Override
	public ImmutableSet<T> clone()
	{
		return new ImmutableSet<T>(startingNode);
	}

	/*
	 * Private field factories.
	 * These should be used in place of new SomeSet<>();
	 * to ensure a consistent internal field implementation.
	 */

	private Set<T> newSet()
	{
		return new CopyOnWriteArraySet<>();
	}

	/*
	 * Basic set actions
	 */

	/**
	 * @return The number of elements in the set.
	 */
	public int size()
	{
		return toSet().size();
	}

	/**
	 * @param item The item to test.
	 * @return True iff item is in the set, false otherwise.
	 */
	public boolean contains(T item)
	{
		return toSet().contains(item);
	}

	/**
	 * Add an item to a copy of this set, then return that set.
	 * If the item is present in the current set then the returned
	 * set is .equal() to this one.
	 * @param item The item to add.
	 * @return A new immutable set with item in it.
	 */
	public ImmutableSet<T> add(T item)
	{
		return newSetWithNode(LazyAction.Add, item);
	}
	
	/**
	 * Create a new set such that item is not present in the set.
	 * If the item is not present in the current set then the returned
	 * set is .equal() to this one.
	 * @param item The item to remove.
	 * @return A new immutable set without item in it.
	 */
	public ImmutableSet<T> remove(T item)
	{
		return newSetWithNode(LazyAction.Remove, item);
	}
	
	private ImmutableSet<T> newSetWithNode(LazyAction action, T item)
	{
		ImmutableSet<T> other = this.clone();
		Node<T> newNode = new Node<>(item, action, other.startingNode);
		
		newNode.addSet(other);
		other.startingNode = newNode;
		
		return other;
	}

	/**
	 * Get the union of two sets.
	 * @param set
	 * @return
	 */
	public ImmutableSet<T> union(ImmutableSet<T> set)
	{
		ImmutableSet<T> newSet = this.clone();

		for (T item : set.startingNode)
		{
			newSet = newSet.add(item);
		}

		return newSet;
	}

	/*
	 * Object contract and niceties
	 */

	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;

		if (o instanceof ImmutableSet<?>)
		{
			ImmutableSet<?> other = (ImmutableSet<?>) o;
			isEqual = this.toSet().equals(other.toSet());
		}

		return isEqual;
	}

	@Override
	public int hashCode()
	{
		return this.toSet().hashCode();
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("{");
		
		for (T item : startingNode)
		{
			s.append(item.toString());
			s.append(", ");
		}
		
		s.delete(s.length() - 2, s.length());
		s.append("}");
		
		return s.toString();
	}

	/*
	 * Basic actions provided by the stream interface.
	 * These are provided here for simplicity on the calling end.
	 * The stream interface can still be used using fromStream.
	 */

	/**
	 * Get a stream from the ImmutableSet.
	 * 
	 * Some basic operations are implemented 
	 * within this interface so it may be easier to use those.
	 */
	public Stream<T> stream()
	{
		return this.clone().toSet().stream();
	}

	/**
	 * Use this method to get an ImmutableSet from a stream.
	 * 
	 * Note that this is a terminal operation on the stream.
	 * 
	 * This is needed because ImmutableSet is not a Collection
	 * so callers can't use Collectors.toCollection(ImmutableSet::new).
	 * We can't implement the collections interface because it
	 * assumes mutation.
	 * @param stream
	 * @return
	 */
	public static <T> ImmutableSet<T> fromStream(Stream<T> stream)
	{
		return new ImmutableSet<T>(stream.collect(Collectors.toSet()));
	}

	/**
	 * A wrapper for Stream.map returning an ImmutableSet.
	 * This function is provided for convenience on the calling end.
	 * @param mapper
	 * @return
	 */
	public <R> ImmutableSet<R> map(Function<? super T, ? extends R> mapper)
	{
		return ImmutableSet.fromStream(this.stream().map(mapper));
	}

	/**
	 * A wrapper for Stream.filter returning an ImmutableSet.
	 * This function is provided for convenience on the calling end.
	 * @param mapper
	 * @return
	 */
	public ImmutableSet<T> filter(Predicate<? super T> predicate)
	{
		return ImmutableSet.fromStream(this.stream().filter(predicate));
	}
	
	/**
	 * Applies the necessary actions to the current set
	 * Both changes and elements are mutated, but this is acceptable
	 * because the difference is not visible to the user.
	 * @return
	 */
	private Set<T> toSet()
	{
		Set<T> set = newSet();
		Set<T> seenElements = newSet();
		
		for (Node<T> node = startingNode; node.parent.isPresent(); node = node.parent.get())
		{
			T element = node.element.get();
			
			if (node.action.get().equals(LazyAction.Remove))
			{
				seenElements.add(element);
			}
			
			if (seenElements.contains(element))
			{
				
				continue; // Whether an add or remove, the later decision (which we have already seen) overrides it
			}

			seenElements.add(element);
			set.add(node.element.get());
		}
		
		return set;
	}
}
