package datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BinaryOperator;
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
	 * The places where we need to make changes that have been requested are:
	 * - contains
	 * - equals
	 * - hashCode
	 * - toString
	 * - stream
	 * 
	 * Others, without additional machinery:
	 * - size
	 * 
	 * Cloning:
	 * The clone of this set is the carrying over of these changes and elements
	 * to new inner structures in a new set. We don't need to apply the changes
	 * because the user still hasn't requested anything from us.
	 * 
	 * A note on streams:
	 * Streams need to be done before the stream call because callers
	 * can collect the stream into another structure without going through
	 * us so all of our changes need to be present within the stream.
	 */

	/**
	 * An action, called lazily, that mutates the internal set `elements'.
	 * The actions associated with each LazyAction may be called immediately,
	 * or may never be called. They are guaranteed to be called either 0 or 1 times
	 * for each time that they are added to the collection of changes to be made.
	 */
	private enum LazyAction
	{
		Add,
		Remove;

		// It is vitally important that the following case statement is kept in sync with
		// the values in this enum.

		// Enums are not allowed to use generics, so we cannot add elements.f(T item) to 
		// the constructor of each enum (which would be the preferred solution). Instead we 
		// maintain a case statement.

		public static <T> void executeAction(Set<T> elements, T item, LazyAction value)
		{
			switch (value)
			{
				case Add:
					elements.add(item);
					break;
				case Remove:
					elements.remove(item);
					break;
			}
		}
	}

	private final Set<T> elements;
	private final Map<T, LazyAction> changes;

	public ImmutableSet()
	{
		this.elements = newSet();
		this.changes = newActionMap();
	}

	public ImmutableSet(Collection<T> items)
	{
		this.elements = newSet(items);
		this.changes = newActionMap();
	}

	private ImmutableSet(Collection<T> items, Map<T, LazyAction> changes)
	{
		this.elements = newSet(items);
		this.changes = newActionMap(changes);
	}

	@Override
	public ImmutableSet<T> clone()
	{
		return new ImmutableSet<T>(elements, changes);
	}


	private Set<T> newSet()
	{
		return new CopyOnWriteArraySet<>();
	}

	private Set<T> newSet(Collection<T> items)
	{
		return new CopyOnWriteArraySet<>(items);
	}

	private Map<T, LazyAction> newActionMap()
	{
		return new HashMap<>();
	}

	private Map<T, LazyAction> newActionMap(Map<T, LazyAction> items)
	{
		return new HashMap<>(items);
	}

	/*
	 * Basic set actions
	 */

	public int size()
	{
		return applyChangesToElements().size();
	}

	public boolean contains(T item)
	{
		return applyChangesToElements().contains(item);
	}

	public ImmutableSet<T> add(T item)
	{
		return storeLazyAction(item, LazyAction.Add);
	}

	public ImmutableSet<T> remove(T item)
	{
		return storeLazyAction(item, LazyAction.Remove);
	}

	public ImmutableSet<T> union(ImmutableSet<T> set)
	{
		/* Following is a strict union implementation,
		 * the lazy version will look something like:
		ImmutableSet<T> newSet = this;

		for (T item : set)
		{
			newSet = newSet.add(item);
		}

		return newSet;
		 */
		ImmutableSet<T> copy = this.clone();
		copy.elements.addAll(set.applyChangesToElements());
		return copy;
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
			isEqual = this.applyChangesToElements().equals(other.applyChangesToElements());
		}

		return isEqual;
	}

	@Override
	public int hashCode()
	{
		return this.applyChangesToElements().hashCode();
	}

	@Override
	public String toString()
	{
		return this.applyChangesToElements().toString();
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
		return this.clone().applyChangesToElements().stream();
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

	public <R> ImmutableSet<R> map(Function<? super T,? extends R> mapper)
	{
		return ImmutableSet.fromStream(this.stream().map(mapper));
	}

	public ImmutableSet<T> filter(Predicate<? super T> predicate)
	{
		return ImmutableSet.fromStream(this.stream().filter(predicate));
	}

	public Optional<T> reduce(BinaryOperator<T> accumulator)
	{
		return this.stream().reduce(accumulator);
	}

	/**
	 * The standard mechanism for storing actions that are to
	 * be executed lazily.
	 * 
	 * T item needs to be provided because the LazyAction will 
	 * be called with elements and the item. See LazyAction for
	 * more details.
	 * 
	 * The effect of the LazyAction needs to be idempotent
	 * This means that repeated actions make no change.
	 * @param item
	 * @param nextAction
	 * @return
	 */
	private ImmutableSet<T> storeLazyAction(T item, LazyAction nextAction)
	{
		/*
		 * We only need to store the last action taken for a specific item:
		 * Given actions are idempotent
		 * a r a, r a r and their subsets are all possible combinations.
		 * 
		 * Where a is add, and r is remove.
		 * 
		 * if item present:
		 * a = noop
		 * a r = r -> remove
		 * a r a = a -> noop
		 * 
		 * r = r -> remove
		 * r a = a -> noop
		 * r a r = r -> remove
		 * 
		 * if item not present:
		 * a = add
		 * a r = r -> noop
		 * a r a = a -> add
		 * 
		 * r = noop
		 * r a = a -> add
		 * r a r = r -> noop
		 */
		
		ImmutableSet<T> other = this.clone();

		if (other.changes.containsKey(item) || other.changes.containsKey(item))
		{
			if (other.changes.get(item).equals(nextAction))
			{
				return other;
			}
			else
			{
				other.changes.put(item, nextAction);
			}
		}
		else
		{
			other.changes.put(item, nextAction);
		}

		return other;
	}

	/**
	 * Applies the necessary actions to the current set
	 * Both changes and elements are mutated, but this is acceptable
	 * because it is not visible to the user.
	 * @return
	 */
	private Set<T> applyChangesToElements()
	{
		for (Entry<T, LazyAction> entry : this.changes.entrySet())
		{
			LazyAction.executeAction(elements, entry.getKey(), entry.getValue());
		}

		this.changes.clear();

		return elements;
	}
}
