package support.collectionutils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.support.CollectionUtils;

/**
 * Unit tests for the
 * {@link CollectionUtils#deleteAndRemove(Object, Object, java.util.Map) method.}
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class DeleteAndRemoveTest {

	private final static Object KEY = new Object();
	private final static Object VALUE = new Object();
	private final static Object OTHER = new Object();
	private final static Map<Object, Set<Object>> MAP = new HashMap<Object, Set<Object>>();

	@Before
	public void setUp() {
		// reset MAP before every run;
		MAP.clear();
	}

	/**
	 * Argument validation: null key
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAndRemoveNullKey() {
		CollectionUtils.deleteAndRemove(null, VALUE, MAP);
	}

	/**
	 * Argument validation: null value
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAndRemoveNullValue() {
		CollectionUtils.deleteAndRemove(KEY, null, MAP);
	}

	/**
	 * Argument validation: null map
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAndRemoveNullMap() {
		CollectionUtils.deleteAndRemove(KEY, VALUE, null);
	}

	/**
	 * Key not in map
	 */
	@Test
	public void testDeleteNonExistentKey() {
		CollectionUtils.deleteAndRemove(KEY, VALUE, MAP);
	}

	/**
	 * Key in map, but Value not in set.
	 */
	@Test
	public void testDeleteNonExistentValue() {
		final Set<Object> set = new HashSet<Object>();
		set.add(OTHER);
		MAP.put(KEY, set);
		CollectionUtils.deleteAndRemove(KEY, VALUE, MAP);
		assertTrue(MAP.containsKey(KEY));
		assertTrue(MAP.get(KEY).contains(OTHER));
	}

	/**
	 * Key in map, and Value in set. No other values in set. Key should be
	 * removed.
	 */
	@Test
	public void testRemovedKeyAfterLastValueDeleted() {
		final Set<Object> set = new HashSet<Object>();
		set.add(VALUE);
		MAP.put(KEY, set);
		CollectionUtils.deleteAndRemove(KEY, VALUE, MAP);
		assertFalse(
				"'Key' should have been removed after the last 'value' has been deleted.",
				MAP.containsKey(KEY));
	}

	/**
	 * Key in map, and Value in set, other Value also in Set. Key should not be
	 * removed.
	 */
	@Test
	public void testDeleteExistentValue() {
		final Set<Object> set = new HashSet<Object>();
		set.add(VALUE);
		set.add(OTHER);
		MAP.put(KEY, set);
		CollectionUtils.deleteAndRemove(KEY, VALUE, MAP);
		assertTrue(
				"'Key' should not have been removed after the a 'value' has been deleted while other 'values' remain in the Set.",
				MAP.containsKey(KEY));
		assertTrue(MAP.get(KEY).contains(OTHER));
		assertFalse(MAP.get(KEY).contains(VALUE));
	}

}
