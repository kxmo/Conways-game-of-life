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
	 * Internal structure:
	 * 
	 * This set uses a trie like structure to maintain immutability of it's elements.
	 * A trie is a tree that stores each element of it's contained values in separate
	 * nodes. A trie contiaining 'as' and 'at' and 'app' would look like:
	 * 
	 *            a
	 *           /|\
	 *          s t p
	 *              |
	 *              p
	 * 
	 * Where the trie is walked downwards.
	 * 
	 * Our trie differs in that we walk upwards.
	 * Each set has a starting node pointing at a particular value and 
	 * all of a particular set's elements are above it.
	 * 
	 * Two sets (s1, s2) containing: 1 and 4, and 1 and 2 may look like (simplified):
	 * 
	 *            1
	 *          /   \
	 *       s1:4  s2:2
	 * 
	 * Creating a new set from s2 by calling: s3 = s2.add(31); makes the trie look like:
	 *          s0:1
	 *          /   \
	 *       s1:4  s2:2
	 *               |
	 *             s3:31
	 * 
	 * The more recent states of a set are closer to the starting node and thus can be 
	 * considered the 'current' state despite the set containing all previous actions.
	 */
	
	/*
	 * Details of the backing trie:
	 * 
	 * There is always at least 1 empty node (e) and a set associated with it at the root.
	 * 
	 * Any node can have multiple branches:
	 * 
	 * w = new ImmutableSet<>();
	 * x = w.add(1);
	 * y = w.add(2);
	 * z = x.add(3);
	 * 
	 * y and x branch of x and do not contain each other's elements:
	 *                   w:e
	 *                   / \
	 *                 x:1 y:2
	 *                 /
	 *               z:3
	 * 
	 * The backing trie is not static (because we cannot statically reference T) but
	 * is passed though as the same reference to all instances. In this way we treat
	 * the backing trie as static. The only case where we do not have a single instance
	 * of the trie is where the caller creates a new ImmutableSet<>():
	 * 
	 * w = new ImmutableSet<>();
	 * x = w.add(1);
	 * y = w.add(2);
	 * z = x.add(3);
	 * 
	 * a = new ImmutableSet<>();
	 * b = a.add(1);
	 *                   w:e        a:e
	 *                   / \        /
	 *                 x:1 y:2     b:1
	 *                 /
	 *               z:3
	 * 
	 * Notice that although x and b are .equal they do not share the same nodes, or even
	 * the same e instance. This is again because e cannot be static because we cannot 
	 * statically reference T, and it is not possible to pass around an e instance on a 
	 * new call.
	 */
	

	/**
	 * A change to an existing set, from which a new set is created.
	 */
	private enum Action
	{
		Add, Remove;
	}
	
	/**
	 * A node of the trie.
	 * 
	 * Elements are either all Optional.empty()
	 * or all present. Nodes are only Optional.empty()
	 * at the root of the trie.
	 * 
	 * @param <E> T in the outer set, but a different
	 * generic variable for generality.
	 */
	private class Node<E> implements Iterable<E>
	{
		private final Optional<E> element;
		private final Optional<Action> action;
		private final Optional<Node<E>> parent;
		private final Set<ImmutableSet<T>> sets;
		
		public Node()
		{
			this.element = Optional.empty();
			this.parent = Optional.empty();
			this.action = Optional.empty();
			this.sets = new CopyOnWriteArraySet<>();
		}
		
		public Node(E element, Action action, Node<E> parent)
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
		this.startingNode = new Node<>(empty);
	}
	
	public ImmutableSet(Node<T> startingNode)
	{
		this.startingNode = new Node<T>(startingNode);
	}

	public ImmutableSet(Collection<T> items)
	{
		ImmutableSet<T> set = new ImmutableSet<>();
		
		for (T item : items)
		{
			set = newSetWithNode(Action.Add, item, set);
		}
		
		this.startingNode = set.startingNode;
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
	 * Determine whether the set is empty.
	 * @return True iff size is equal to 0, false otherwise.
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

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
		return newSetWithNode(Action.Add, item, this);
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
		return newSetWithNode(Action.Remove, item, this);
	}
	
	private ImmutableSet<T> newSetWithNode(Action action, T item, ImmutableSet<T> set)
	{
		ImmutableSet<T> other = set.clone();
		Node<T> newNode = new Node<>(item, action, other.startingNode);
		
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
	 * Get the current ImmutableSet as a Set conforming to the set interface.
	 * All elements are copied so this set may be modified.
	 * @return
	 */
	public Set<T> toSet()
	{
		Set<T> set = newSet();
		Set<T> seenElements = newSet();
		
		for (Node<T> node = startingNode; node.parent.isPresent(); node = node.parent.get())
		{
			T element = node.element.get();
			
			if (node.action.get().equals(Action.Remove))
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
