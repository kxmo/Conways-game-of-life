package datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImmutableSet<T>
{
	private final Set<T> set;
	
	public ImmutableSet()
	{
		this.set = Collections.emptySet();
	}
	
	public ImmutableSet(Collection<T> items)
	{
		Set<T> copy = Collections.emptySet();
		copy.addAll(items);
		
		this.set = copy;
	}

	public ImmutableSet(ImmutableSet<T> items)
	{
		this(items.set);
	}

	public ImmutableSet<T> copy(ImmutableSet<T> set)
	{
		return new ImmutableSet<T>(set.set);
	}
	
	
	/*
	 * Basic set actions
	 */
	
	public int size()
	{
		return this.set.size();
	}
	
	public boolean contains(T item)
	{
		return this.set.contains(item);
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
		return safeAction(s -> s.addAll(set.set));
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof ImmutableSet<?>)
		{
			ImmutableSet<?> other = (ImmutableSet<?>) o;
			isEqual = this.set.equals(other.set);
		}
		
		return isEqual;
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
		return copy(this).set.stream();
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
		ImmutableSet<T> copy = copy(this);
		action.accept(copy.set);
		return copy;
	}
}
