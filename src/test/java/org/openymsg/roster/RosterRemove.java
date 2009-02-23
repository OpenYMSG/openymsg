package org.openymsg.roster;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openymsg.network.FriendManager;
import org.openymsg.network.YahooUser;

/**
 * Checks for {@link Roster#remove(org.openymsg.network.YahooUser)}, calling
 * which should result into outgoing packets to the yahoo network, indicating
 * that you want to add a contact from your roster.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */

public abstract class RosterRemove<T extends Roster<U>, U extends YahooUser> {

	/**
	 * Roster makes use of a FriendManager to relay calls to Yahoo. This test
	 * checks if calling 'remove' triggers the FriendManager.
	 */
	@Test
	public void testCallingRemoveTriggersFriendManager() {
		// setup
		final MockFriendManager manager = new MockFriendManager();
		final T roster = createRoster(manager);
		final U user = createUser("user", "group");
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
	 * This test checks if calling 'remove' for a friend that does not exist,
	 * does not trigger the FriendManager.
	 */
	@Test
	public void testCallingRemoveOnNonExistingUserDoesNotTriggerFriendManager() {
		// setup
		final MockFriendManager manager = new MockFriendManager();
		final T roster = createRoster(manager);
		final U user = createUser("user", "group");

		// execution
		final boolean result = roster.remove(user);

		// validation
		assertFalse(result);
		assertNull(manager.getMethod());
		assertNull(manager.getFriendId());
		assertNull(manager.getGroupId());
	}
	
	protected abstract T createRoster(final FriendManager manager);

	protected abstract U createUser(String userId, String group);
}
