package org.openymsg.roster;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.network.FriendManager;
import org.openymsg.network.YahooUser;

/**
 * Bulk operations on the Roster object are not allowed. This testcase tests if
 * the bulk methods inherited by the Set interface throw the appropriate
 * exceptions.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public abstract class RosterBulkOperationsForbidden<T extends Roster<U>, U extends YahooUser> {

	protected T roster;
	private Set<U> testUsers;

	@Before
	public void setUp() throws Throwable {
		roster = createRoster(new MockFriendManager());

		testUsers = new HashSet<U>();
		testUsers.add(createUser("test"));
		testUsers.add(createUser("user"));
	}

	protected abstract U createUser(String userId);
	
	protected abstract T createRoster(final FriendManager manager);

	/**
	 * Test method for {@link org.openymsg.roster.Roster#iterator()}. Changes
	 * through the iterator should not be allowed.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testIteratorDotRemove() {
		final Iterator<U> iter = roster.iterator();
		iter.next();
		iter.remove();
	}

	/**
	 * Test method for
	 * {@link org.openymsg.roster.Roster#removeAll(java.util.Collection)}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveAll() {
		roster.removeAll(testUsers);
	}

	/**
	 * Test method for
	 * {@link org.openymsg.roster.Roster#retainAll(java.util.Collection)}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRetainAll() {
		roster.retainAll(testUsers);
	}

	/**
	 * Test method for
	 * {@link org.openymsg.roster.Roster#addAll(java.util.Collection)}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAll() {
		roster.addAll((Collection<? extends U>) testUsers);
	}
}
