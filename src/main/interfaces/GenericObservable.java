package main.interfaces;

import java.util.HashSet;
import java.util.Set;

/**
 * A minimal typesafe observer implementation.
 * @param <T> The type of the data being observed.
 */
public abstract class GenericObservable<T>
{
	private final Set<GenericObserver<T>> observers = new HashSet<>();
	
	public void addObserver(GenericObserver<T> o)
	{
		observers.add(o);
	}
	
	public void deleteObserver(GenericObserver<T> o)
	{
		observers.remove(o);
	}

	public void notifyObservers(T arg)
	{
		for (GenericObserver<T> o : observers)
		{
			o.update(arg);
		}
	}
}
