/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.legacy.support;

import java.util.Map;
import java.util.Set;

/**
 * Various utility methods that perform actions on Collections.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class CollectionUtils {

    /**
     * Removes the value from the set of values mapped by key. If this value was the last value in the set of values
     * mapped by key, the complete key/values entry is removed.
     * 
     * @param key
     *            The key for which to remove a value.
     * @param value
     *            The value for which to remove the key.
     * @param map
     *            The object that maps the key to a set of values, on which the operation should occur.
     */
    public static <K, V> void deleteAndRemove(final K key, final V value, final Map<K, Set<V>> map) {
        if (key == null) {
            throw new IllegalArgumentException("Argument 'key' cannot be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Argument 'value' cannot be null.");
        }
        if (map == null) {
            throw new IllegalArgumentException("Argument 'map' cannot be null.");
        }

        if (map.isEmpty() || !map.containsKey(key)) {
            return;
        }

        final Set<V> values = map.get(key);
        values.remove(value);

        if (values.isEmpty()) {
            map.remove(key);
        }
    }
}
