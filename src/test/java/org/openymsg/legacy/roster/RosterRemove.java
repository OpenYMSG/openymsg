package org.openymsg.legacy.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openymsg.legacy.network.YahooProtocol;
import org.openymsg.legacy.network.YahooUser;

/**
 * Checks for {@link Roster#remove(org.openymsg.legacy.network.YahooUser)}, calling which should result into outgoing
 * packets to the yahoo network, indicating that you want to add a contact from your roster.
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterRemove {
	/**
	 * Roster makes use of a FriendManager to relay calls to Yahoo. This test checks if calling 'remove' triggers the
	 * FriendManager.
	 */
	@Test
	public void testCallingRemoveTriggersFriendManager() {
		// setup
		final MockFriendManager manager = new MockFriendManager();
		final Roster roster = new Roster(manager);
		final YahooUser user = new YahooUser("user", "group", YahooProtocol.YAHOO);
		roster.add(user);
		manager.reset();
		// execution
		final boolean result = roster.remove(user);
		// validation
		assertTrue(result);
		assertEquals("removeFriendFromGroup", manager.getMethod());
		assertEquals("user", manager.getFriendId());
		assertEquals("group", manager.getGroupId());
	}

	/**
	 * This test checks if calling 'remove' for a friend that does not exist, does not trigger the FriendManager.
	 */
	@Test
	public void testCallingRemoveOnNonExistingUserDoesNotTriggerFriendManager() {
		// setup
		final MockFriendManager manager = new MockFriendManager();
		final Roster roster = new Roster(manager);
		final YahooUser user = new YahooUser("user", "group", YahooProtocol.YAHOO);
		// execution
		final boolean result = roster.remove(user);
		// validation
		assertFalse(result);
		assertNull(manager.getMethod());
		assertNull(manager.getFriendId());
		assertNull(manager.getGroupId());
	}
}
