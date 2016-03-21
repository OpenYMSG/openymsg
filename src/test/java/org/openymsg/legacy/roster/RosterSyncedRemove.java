package org.openymsg.legacy.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.legacy.network.YahooUser;

import java.util.Iterator;

import junitx.util.PrivateAccessor;

/**
 * Test method for {@link org.openymsg.legacy.roster.Roster#syncedRemove(String userId)}. This testcase checks if
 * removing items from the roster persists that change on the roster.
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterSyncedRemove {
	private final static YahooUser USER = new YahooUser("dummy");
	private Roster roster;

	/**
	 * Initializes the roster before each test.
	 * @throws Throwable
	 */
	@Before
	public void setUp() throws Throwable {
		roster = new Roster(new MockFriendManager());
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] {YahooUser.class}, new Object[] {USER});
	}

	/**
	 * Checks if removing a user to the roster doesn't throw any runtime exceptions.
	 * @throws Throwable
	 */
	@Test
	public void testSimpleRemove() throws Throwable {
		PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class}, new Object[] {USER.getId()});
	}

	/**
	 * Checks if removing an existing user returns <tt>true</tt>.
	 * @throws Throwable
	 */
	@Test
	public void testReturnTrueAfterRemovingUser() throws Throwable {
		final Object returnValue =
				PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class}, new Object[] {USER.getId()});
		assertTrue((Boolean) returnValue);
	}

	/**
	 * Checks if removing a non-existing user returns <tt>false</tt>.
	 * @throws Throwable
	 */
	@Test
	public void testReturnFalseAfterRemovingNonExistingUser() throws Throwable {
		final Object returnValue = PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class},
				new Object[] {"doesntexist"});
		assertFalse((Boolean) returnValue);
	}

	/**
	 * Checks if the size method returns an increment after a user has been added.
	 * @throws Throwable
	 */
	@Test
	public void testRemoveReflectedInSize() throws Throwable {
		final int oldSize = roster.size();
		PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class}, new Object[] {USER.getId()});
		assertEquals(oldSize - 1, roster.size());
	}

	/**
	 * Checks if an iterator of the roster no longer contains the removed user on the roster.
	 * @throws Throwable
	 */
	@Test
	public void testRemoveContainedInIterator() throws Throwable {
		PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class}, new Object[] {USER.getId()});
		final Iterator<YahooUser> iter = roster.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(USER)) {
				fail("Iterator still includes the removed user.");
			}
		}
	}

	/**
	 * Make sure that the contains() method no longeer recognizes the removed user.
	 * @throws Throwable
	 */
	@Test
	public void testRemoveRecognizedByCointains() throws Throwable {
		assertTrue(roster.contains(USER));
		PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class}, new Object[] {USER.getId()});
		assertFalse(roster.contains(USER));
	}

	/**
	 * Make sure that the containsUser() method recognizes the added user.
	 * @throws Throwable
	 */
	@Test
	public void testRemoveRecognizedByCointainsUser() throws Throwable {
		assertTrue(roster.containsUser(USER.getId()));
		PrivateAccessor.invoke(roster, "syncedRemove", new Class[] {String.class}, new Object[] {USER.getId()});
		assertFalse(roster.containsUser(USER.getId()));
	}
}
