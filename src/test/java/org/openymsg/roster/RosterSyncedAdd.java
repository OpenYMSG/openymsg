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
 * {@link org.openymsg.roster.Roster#syncedAdd(org.openymsg.network.YahooUser)}.
 * This testcase checks if adding items to the roster persists those items on
 * the roster.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public abstract class RosterSyncedAdd<T extends Roster<U>, U extends YahooUser> {

	private U user;
	private T roster;

	/**
	 * Initializes the roster before each test.
	 */
	@Before
	public void setUp() {
		user = createUser("dummy");
		roster = createRoster(new MockFriendManager());
	}

	/**
	 * Checks if adding a user to the roster doesn't throw any runtime
	 * exceptions.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testSimpleAdd() throws Throwable {
		addUserToRoster(roster, user);
	}

	/**
	 * Checks if adding a non-existing user returns <tt>true</tt>.
	 */
	@Test
	public void testReturnTrueAfterAddingNonExistingUser() throws Throwable {
		boolean returnValue = addUserToRoster(roster, user);
		assertTrue(returnValue);
	}

	/**
	 * Checks if adding an existing user returns <tt>false</tt>.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testReturnFalseAfterAddingExistingUser() throws Throwable {
		addUserToRoster(roster, user);
		boolean returnValue = addUserToRoster(roster, user);
		assertTrue(returnValue);
	}

	/**
	 * Checks if the size method returns an increment after a user has been
	 * added.
	 * @throws Throwable 
	 */
	@Test
	public void testAddReflectedInSize() throws Throwable {
		final int oldSize = roster.size();
		addUserToRoster(roster, user);
		assertEquals(oldSize + 1, roster.size());
	}

	/**
	 * Checks if an iterator of the roster contains the added user on the
	 * roster.
	 * @throws Throwable 
	 */
	@Test
	public void testAddContainedInIterator() throws Throwable {
		addUserToRoster(roster, user);
		final Iterator<U> iter = roster.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(user)) {
				return; // success
			}
		}
		fail("Iterator didn't include user.");
	}

	/**
	 * Make sure that the contains() method recognizes the added user.
	 * @throws Throwable 
	 */
	@Test
	public void testAddRecognizedByCointains() throws Throwable {
		assertFalse(roster.contains(user));
		addUserToRoster(roster, user);
		assertTrue(roster.contains(user));
	}

	/**
	 * Make sure that the containsUser() method recognizes the added user.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testAddRecognizedByCointainsUser() throws Throwable {
		assertFalse(roster.containsUser(user.getId()));
		addUserToRoster(roster, user);
		assertTrue(roster.containsUser(user.getId()));
	}

	protected abstract boolean addUserToRoster(T roster, U user) throws Throwable;

	protected abstract T createRoster(final FriendManager manager);

	protected abstract U createUser(String userId);

}
