package org.openymsg.roster;

import org.junit.Test;

/**
 * Test case for the constructor(s) of the {@link Roster} class.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterCreationTest {

    /**
     * A default way of using the constructor should not cause any problems.
     */
    @Test
    public void testRosterConstructor() {
        new Roster(new MockFriendManager());
    }

    /**
     * Passing 'null' as a FriendManager is invalid, and should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRosterConstructorWithNullFriendManager() {
        new Roster(null);
    }

}
