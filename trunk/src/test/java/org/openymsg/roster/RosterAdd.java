package org.openymsg.roster;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openymsg.network.YahooUser;

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
		final Roster roster = new Roster(manager);
		final YahooUser user = new YahooUser("user", "group");

		// execution
		roster.add(user);

		// validation
		assertEquals("sendNewFriendRequest", manager.getMethod());
		assertEquals("user", manager.getFriendId());
		assertEquals("group", manager.getGroupId());
	}

}
