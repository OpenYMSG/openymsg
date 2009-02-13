package org.openymsg.roster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.network.YahooUser;
import org.openymsg.v1.network.YahooUserV1;
import org.openymsg.v1.roster.RosterV1;

/**
 * Bulk operations on the Roster object are not allowed. This testcase tests if
 * the bulk methods inherited by the Set interface throw the appropriate
 * exceptions.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterBulkOperationsForbidden {

	private Roster roster;
	private Set<YahooUser> testUsers;

	@Before
	public void setUp() throws Throwable {
		roster = new RosterV1(new MockFriendManager());
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] { YahooUser.class },
				new Object[] { new YahooUserV1("on roster") });
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] { YahooUser.class },
				new Object[] { new YahooUserV1("on roster as well") });

		testUsers = new HashSet<YahooUser>();
		testUsers.add(new YahooUserV1("test"));
		testUsers.add(new YahooUserV1("user"));
	}

	/**
	 * Test method for {@link org.openymsg.roster.Roster#iterator()}. Changes
	 * through the iterator should not be allowed.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testIteratorDotRemove() {
		final Iterator<YahooUser> iter = roster.iterator();
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
		roster.addAll(testUsers);
	}
}
