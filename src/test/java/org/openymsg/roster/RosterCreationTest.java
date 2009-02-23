package org.openymsg.roster;

import org.junit.Test;
import org.openymsg.network.FriendManager;
import org.openymsg.network.YahooUser;

/**
 * Test case for the constructor(s) of the {@link Roster} class.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public abstract class RosterCreationTest<T extends Roster<? extends YahooUser>> {

	/**
	 * A default way of using the constructor should not cause any problems.
	 */
	@Test
	public void testRosterConstructor() {
		createRoster(new MockFriendManager());
	}

	/**
	 * Passing 'null' as a FriendManager is invalid, and should cause an
	 * exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterConstructorWithNullFriendManager() {
		createRoster(null);
	}
	
	protected abstract T createRoster(final FriendManager manager);

}
