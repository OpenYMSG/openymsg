package org.openymsg.roster;

import org.junit.Test;
import org.openymsg.v1.roster.RosterV1;

/**
 * Checks if adding and removing RosterListeners to the Roster operates as
 * expected.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class RosterListenerManagementTest {

	/**
	 * Trying to pass 'null' to {@link Roster#addRosterListener()} should throw
	 * an IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotAddNull() {
		new RosterV1(new MockFriendManager()).addRosterListener(null);
	}

	/**
	 * Trying to pass 'null' to {@link Roster#removeRosterListener()} should
	 * throw an IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotRemoveNull() {
		new RosterV1(new MockFriendManager()).removeRosterListener(null);
	}
}
