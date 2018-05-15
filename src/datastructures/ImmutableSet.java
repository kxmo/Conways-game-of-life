package datastructures;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
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
	private final Set<T> elements;
	
	public ImmutableSet()
	{
		this.elements = new CopyOnWriteArraySet<>();
	}
	
	public ImmutableSet(Collection<T> items)
	{
		Set<T> copy = new CopyOnWriteArraySet<>(items);
		this.elements = copy;
	}

	@Override
	public ImmutableSet<T> clone()
	{
		return new ImmutableSet<T>(elements);
	}
	
	
	/*
	 * Basic set actions
	 */
	
	public int size()
	{
		return this.elements.size();
	}
	
	public boolean contains(T item)
	{
		return this.elements.contains(item);
	}
	
	public ImmutableSet<T> add(T item)
	{
		return safeAction(s -> s.add(item));
	}
	
	public ImmutableSet<T> remove(T item)
	{
		return safeAction(s -> s.remove(item));
	}
	
	public ImmutableSet<T> union(ImmutableSet<T> set)
	{
		return safeAction(s -> s.addAll(set.elements));
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof ImmutableSet<?>)
		{
			ImmutableSet<?> other = (ImmutableSet<?>) o;
			isEqual = this.elements.equals(other.elements);
		}
		
		return isEqual;
	}
	
	@Override
	public int hashCode()
	{
		return elements.hashCode();
	}
	
	@Override
	public String toString()
	{
		return this.elements.toString();
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
		return this.clone().elements.stream();
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
	 * Execute an action on the copy's internal set.
	 * @param action The action to execute. Must mutate the given set's state to have an effect.
	 * @return A copy of `this' with the contents modified according to action.
	 */
	private ImmutableSet<T> safeAction(Consumer<Set<T>> action)
	{
		ImmutableSet<T> copy = this.clone();
		action.accept(copy.elements);
		return copy;
	}
}
