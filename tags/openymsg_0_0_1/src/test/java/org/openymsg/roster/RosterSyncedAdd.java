package org.openymsg.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
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
public class RosterSyncedAdd {

	private final static YahooUser USER = new YahooUser("dummy");
	private Roster roster;

	/**
	 * Initializes the roster before each test.
	 */
	@Before
	public void setUp() {
		roster = new Roster(new MockFriendManager());
	}

	/**
	 * Checks if adding a user to the roster doesn't throw any runtime
	 * exceptions.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testSimpleAdd() throws Throwable {
		PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
	}

	/**
	 * Checks if adding a non-existing user returns <tt>true</tt>.
	 */
	@Test
	public void testReturnTrueAfterAddingNonExistingUser() throws Throwable {
		final Object returnValue = PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		assertTrue((Boolean) returnValue);
	}

	/**
	 * Checks if adding an existing user returns <tt>false</tt>.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testReturnFalseAfterAddingExistingUser() throws Throwable {
		PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		final Object returnValue = PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		assertFalse((Boolean) returnValue);
	}

	/**
	 * Checks if the size method returns an increment after a user has been
	 * added.
	 * @throws Throwable 
	 */
	@Test
	public void testAddReflectedInSize() throws Throwable {
		final int oldSize = roster.size();
		PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		assertEquals(oldSize + 1, roster.size());
	}

	/**
	 * Checks if an iterator of the roster contains the added user on the
	 * roster.
	 * @throws Throwable 
	 */
	@Test
	public void testAddContainedInIterator() throws Throwable {
		PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		final Iterator<YahooUser> iter = roster.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(USER)) {
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
		assertFalse(roster.contains(USER));
		PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		assertTrue(roster.contains(USER));
	}

	/**
	 * Make sure that the containsUser() method recognizes the added user.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testAddRecognizedByCointainsUser() throws Throwable {
		assertFalse(roster.containsUser(USER.getId()));
		PrivateAccessor.invoke(roster, "syncedAdd",
				new Class[] { YahooUser.class }, new Object[] { USER });
		assertTrue(roster.containsUser(USER.getId()));
	}
}
