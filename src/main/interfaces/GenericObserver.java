package main.interfaces;

/**
 * A minimal typesafe observer.
 * @param <T> The type of data received from the observable.
 */
public interface GenericObserver<T>
{
	/**
	 * A new instance of data from the observable.
	 * @param arg The data received.
	 */
	public void update(T arg);
}
