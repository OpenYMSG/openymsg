package org.openymsg.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openymsg.v1.network.YahooUserV1;
import org.openymsg.v1.roster.RosterV1;

/**
 * Checks for {@link Roster#add(org.openymsg.network.YahooUser)}, calling which
 * should result into outgoing packets to the yahoo network, indicating that you
 * want to add a contact to your roster.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class RosterAdd {

	/**
	 * Roster makes use of a FriendManager to relay calls to Yahoo. This test
	 * checks if calling 'add' triggers the FriendManager.
	 */
	@Test
	public void testCallingAddTriggersFriendManager() {
		// setup
		final MockFriendManager manager = new MockFriendManager();
		final RosterV1 roster = new RosterV1(manager);
		final YahooUserV1 user = new YahooUserV1("user", "group");

		// execution
		final boolean result = roster.add(user);

		// validation
		assertTrue(result);
		assertEquals("sendNewFriendRequest", manager.getMethod());
		assertEquals("user", manager.getFriendId());
		assertEquals("group", manager.getGroupId());
	}

	/**
	 * Checks that adding a user to the roster that already exists on the roster
	 * does not trigger the FriendManager.
	 */
	@Test
	public void testCallingAddTwiceDoesntTriggerFriendManager() {
		// setup
		final MockFriendManager manager = new MockFriendManager();
		final RosterV1 roster = new RosterV1(manager);
		final YahooUserV1 user = new YahooUserV1("user", "group");
		roster.add(user); // first time
		manager.reset();

		// execution
		final boolean result = roster.add(user);

		// validation
		assertFalse(result);
		assertEquals("sendNewFriendRequest", manager.getMethod());
		assertEquals("user", manager.getFriendId());
		assertEquals("group", manager.getGroupId());
	}
}
