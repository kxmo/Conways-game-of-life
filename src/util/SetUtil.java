package util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetUtil
{
	public static <T> Set<T> newSet()
	{
		return Collections.emptySet();
	}
	
	public static <T> Set<T> copy(Set<T> set)
	{
		return new HashSet<T>(set);
	}
	
	public static <T> Set<T> remove(Set<T> set, T item)
	{
		Set<T> copy = SetUtil.copy(set);
		copy.remove(item);
		return copy;
	}
	
	public static <T> Set<T> add(Set<T> set, T item)
	{
		Set<T> copy = SetUtil.copy(set);
		copy.add(item);
		return copy;
	}
}
