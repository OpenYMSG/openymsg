package org.openymsg.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.network.FriendManager;
import org.openymsg.network.YahooUser;

/**
 * Test method for
 * {@link org.openymsg.roster.Roster#syncedRemove(String userId)}. This
 * testcase checks if removing items from the roster persists that change on the
 * roster.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public abstract class RosterSyncedRemove<T extends Roster<U>, U extends YahooUser> {

	private U user;
	private T roster;

	/**
	 * Initializes the roster before each test.
	 * 
	 * @throws Throwable
	 */
	@Before
	public void setUp() throws Throwable {
		user = createUser("dummy");
		roster = createRoster(new MockFriendManager());
		addUserToRoster(roster, user);
	}

	/**
	 * Checks if removing a user to the roster doesn't throw any runtime
	 * exceptions.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testSimpleRemove() throws Throwable {
		removeUserFromRoster(roster, user);
	}

	/**
	 * Checks if removing an existing user returns <tt>true</tt>.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testReturnTrueAfterRemovingUser() throws Throwable {
		boolean returnValue = removeUserFromRoster(roster, user);
		assertTrue(returnValue);
	}

	/**
	 * Checks if removing a non-existing user returns <tt>false</tt>.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testReturnFalseAfterRemovingNonExistingUser() throws Throwable {
		U otherUser = createUser("doesntexist");
		boolean returnValue = removeUserFromRoster(roster, otherUser);
		assertFalse(returnValue);
	}

	/**
	 * Checks if the size method returns an increment after a user has been
	 * added.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testRemoveReflectedInSize() throws Throwable {
		final int oldSize = roster.size();
		removeUserFromRoster(roster, user);
		assertEquals(oldSize - 1, roster.size());
	}

	/**
	 * Checks if an iterator of the roster no longer contains the removed user
	 * on the roster.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testRemoveContainedInIterator() throws Throwable {
		removeUserFromRoster(roster, user);
		final Iterator<U> iter = roster.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(user)) {
				fail("Iterator still includes the removed user.");
			}
		}
	}

	/**
	 * Make sure that the contains() method no longeer recognizes the removed
	 * user.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testRemoveRecognizedByCointains() throws Throwable {
		assertTrue(roster.contains(user));
		removeUserFromRoster(roster, user);
		assertFalse(roster.contains(user));
	}

	/**
	 * Make sure that the containsUser() method recognizes the added user.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testRemoveRecognizedByCointainsUser() throws Throwable {
		assertTrue(roster.containsUser(user.getId()));
		removeUserFromRoster(roster, user);
		assertFalse(roster.containsUser(user.getId()));
	}

	protected abstract boolean addUserToRoster(T roster, U user) throws Throwable;

	protected abstract boolean removeUserFromRoster(T roster, U user) throws Throwable;

	protected abstract T createRoster(final FriendManager manager);

	protected abstract U createUser(String userId);

}
