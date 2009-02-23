package org.openymsg.roster;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openymsg.network.FriendManager;
import org.openymsg.network.YahooUser;

/**
 * Testcase for the RosterEvent class. This testcase mainly checks argument validation.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public abstract class RosterEventTest<T extends Roster<U>, U extends YahooUser> {

	/**
	 * Argument 'source' of the constructor cannot be null. Test method for
	 * {@link org.openymsg.roster.RosterEvent#RosterEvent(org.openymsg.roster.Roster, org.openymsg.network.YahooUser, org.openymsg.roster.RosterEventType)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterEventArgumentValidationSource() {
		new RosterEvent(null, createUser("dummy"), RosterEventType.add);
	}

	/**
	 * Argument 'user' of the constructor cannot be null. Test method for
	 * {@link org.openymsg.roster.RosterEvent#RosterEvent(org.openymsg.roster.Roster, org.openymsg.network.YahooUser, org.openymsg.roster.RosterEventType)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterEventArgumentValidationYahooUser() {
		new RosterEvent(createRoster(new MockFriendManager()), null, RosterEventType.add);
	}

	/**
	 * Argument 'type' of the constructor cannot be null. Test method for
	 * {@link org.openymsg.roster.RosterEvent#RosterEvent(org.openymsg.roster.Roster, org.openymsg.network.YahooUser, org.openymsg.roster.RosterEventType)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterEventArgumentValidationType() {
		new RosterEvent(createRoster(new MockFriendManager()), createUser("dummy"), null);
	}

	/**
	 * Test method for {@link org.openymsg.roster.RosterEvent#getUser()}.
	 */
	@Test
	public void testGetUser() {
		final YahooUser user = createUser("dummy");

		// should work for all RosterEventTypes
		for (final RosterEventType type : RosterEventType.values()) {
			assertEquals("failed on type=" + type, user, new RosterEvent(
					createRoster(new MockFriendManager()), user, type).getUser());
		}
	}

	/**
	 * Test method for {@link org.openymsg.roster.RosterEvent#getType()}.
	 */
	@Test
	public void testGetType() {
		for (final RosterEventType type : RosterEventType.values()) {
			assertEquals(type, new RosterEvent(createRoster(new MockFriendManager()), 
					createUser("dummy"), type).getType());
		}
	}

	/**
	 * Test method for {@link org.openymsg.roster.RosterEvent#getSource()}.
	 */
	@Test
	public void testGetSource() {
		final Roster<? extends YahooUser> roster = createRoster(new MockFriendManager());
		for (final RosterEventType type : RosterEventType.values()) {
			assertEquals(roster, new RosterEvent(roster, createUser("dummy"), type).getSource());
		}
	}
	
	protected abstract T createRoster(final FriendManager manager);

	protected abstract U createUser(String userId);
	

}
