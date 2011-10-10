package org.openymsg.roster;

import org.junit.Test;

/**
 * Checks if adding and removing RosterListeners to the Roster operates as expected.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class RosterListenerManagementTest {

    /**
     * Trying to pass 'null' to {@link Roster#addRosterListener()} should throw an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCannotAddNull() {
        new Roster(new MockFriendManager()).addRosterListener(null);
    }

    /**
     * Trying to pass 'null' to {@link Roster#removeRosterListener()} should throw an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCannotRemoveNull() {
        new Roster(new MockFriendManager()).removeRosterListener(null);
    }
}
