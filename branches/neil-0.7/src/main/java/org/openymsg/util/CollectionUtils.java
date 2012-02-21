package org.openymsg.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {
	
	public static <T> Set<T> protectedSet(Set<? extends T> set) {
		return Collections.unmodifiableSet(new HashSet<T>(set));
	}
	

}
