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
	 * The existing set elements are kept in `elements'.
	 * The changes to be made are kept in a mapping from
	 * the element to the change to be made (a LazyAction).
	 * For more information on why this is valid see storeLazyAction().
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

	/**
	 * Create an empty immutable set
	 */
	public ImmutableSet()
	{
		this.elements = newSet();
		this.changes = newActionMap();
	}

	/**
	 * Create an immutable set with the items from
	 * items.
	 * @param items The items to add to the new set.
	 */
	public ImmutableSet(Collection<T> items)
	{
		this.elements = newSet(items);
		this.changes = newActionMap();
	}

	/**
	 * A new set with the same elements and changes as the old one.
	 * All items are copied into new containing sets/maps so this is
	 * not a very cheap operation.
	 * @param items The existing set elements.
	 * @param changes The pending set changes.
	 */
	private ImmutableSet(Collection<T> items, Map<T, LazyAction> changes)
	{
		this.elements = newSet(items);
		this.changes = newActionMap(changes);
	}

	@Override
	public ImmutableSet<T> clone()
	{
		// We don't need to apply the changes because the user still hasn't requested anything from us.
		return new ImmutableSet<T>(elements, changes);
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

	/**
	 * @return The number of elements in the set.
	 */
	public int size()
	{
		return applyChangesToElements().size();
	}

	/**
	 * @param item The item to test.
	 * @return True iff item is in the set, false otherwise.
	 */
	public boolean contains(T item)
	{
		return applyChangesToElements().contains(item);
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
		return storeLazyAction(item, LazyAction.Add);
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
		return storeLazyAction(item, LazyAction.Remove);
	}

	/**
	 * Get the union of two sets.
	 * @param set
	 * @return
	 */
	public ImmutableSet<T> union(ImmutableSet<T> set)
	{
		/*
		 * The current implementation is more efficient if the
		 * parameter set has a smaller number of changes to make 
	 	 * than the callee set.
	 	 * 
	 	 * A fully lazy approach would combine all of the elements
	 	 * and combine the changes favoring add over remove.
		 */
		ImmutableSet<T> newSet = this;
		
		set.applyChangesToElements();

		for (T item : set.elements)
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

	/**
	 * A wrapper for Stream.map returning an ImmutableSet.
	 * This function is provided for convenience on the calling end.
	 * @param mapper
	 * @return
	 */
	public <R> ImmutableSet<R> map(Function<? super T,? extends R> mapper)
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
	 * A wrapper for Stream.reduce returning an ImmutableSet.
	 * This function is provided for convenience on the calling end.
	 * @param mapper
	 * @return
	 */
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
		 * Actions need to be idempotent to enable lazy execution.
		 * We need to be able to remove some actions from the group of
		 * actions without affecting the outcome. In this case the natural
		 * implementation for add and remove is idempotent because we are
		 * operating on a set (the same is not true, for example, on a list).
		 * 
		 * 
		 * Where a is add, and r is remove.
		 * a r a, r a r and their subsets are all possible combinations due to
		 * idempotency.
		 * 
		 * The form of the proof is:
		 * requested action = result -> end result
		 * a = noop // On add do nothing
		 * r a = a -> noop // On remove then add, do an add which is do nothing
		 * 
		 * Proof:
		 * Assume there are only 2 cases: an item is either present or not present.
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
		 * 
		 * From the truth table above:
		 * The last action taken is the final result of any combination of actions in
		 * every case.
		 * Map.put(x,y) where x and y are already present is a noop which is consistent
		 * with above so may be used in all cases.
		 */
		
		ImmutableSet<T> other = this.clone();
		other.changes.put(item, nextAction);
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
