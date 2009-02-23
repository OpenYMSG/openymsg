package org.openymsg.roster;

import org.junit.Test;
import org.openymsg.network.FriendManager;
import org.openymsg.network.YahooUser;

/**
 * Checks if adding and removing RosterListeners to the Roster operates as
 * expected.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public abstract class RosterListenerManagementTest<T extends Roster<? extends YahooUser>> {

	/**
	 * Trying to pass 'null' to {@link Roster#addRosterListener()} should throw
	 * an IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotAddNull() {
		createRoster(new MockFriendManager()).addRosterListener(null);
	}

	/**
	 * Trying to pass 'null' to {@link Roster#removeRosterListener()} should
	 * throw an IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotRemoveNull() {
		createRoster(new MockFriendManager()).removeRosterListener(null);
	}

	protected abstract T createRoster(final FriendManager manager);

}
