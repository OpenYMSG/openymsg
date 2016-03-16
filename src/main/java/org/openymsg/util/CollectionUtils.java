package org.openymsg.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to handle Collections
 * @author neilhart
 */
public class CollectionUtils {

	/**
	 * Return a protected Set. The Set is not modifiable and will provide a thread-safe iterator if the underlying set
	 * changes. If the underlying Set changes, those changes will not go into the protected Set.
	 * @param set the underlying set to create a protected copy
	 * @return a unmodifiable copy of the underlying set
	 */
	// TODO - copying set isn't protected
	public static <T> Set<T> protectedSet(Collection<? extends T> set) {
		return Collections.unmodifiableSet(new HashSet<T>(set));
	}

	public static <T> Set<T> unmodifiableSet(Set<? extends T> set) {
		return Collections.unmodifiableSet(set);
	}

}
