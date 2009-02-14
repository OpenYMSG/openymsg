package org.openymsg.roster;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openymsg.network.YahooUser;
import org.openymsg.v1.network.YahooUserV1;
import org.openymsg.v1.roster.RosterV1;

/**
 * Testcase for the RosterEvent class. This testcase mainly checks argument validation.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterEventTest {

	/**
	 * Argument 'source' of the constructor cannot be null. Test method for
	 * {@link org.openymsg.roster.RosterEvent#RosterEvent(org.openymsg.roster.Roster, org.openymsg.network.YahooUser, org.openymsg.roster.RosterEventType)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterEventArgumentValidationSource() {
		new RosterEvent(null, new YahooUserV1("dummy"), RosterEventType.add);
	}

	/**
	 * Argument 'user' of the constructor cannot be null. Test method for
	 * {@link org.openymsg.roster.RosterEvent#RosterEvent(org.openymsg.roster.Roster, org.openymsg.network.YahooUser, org.openymsg.roster.RosterEventType)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterEventArgumentValidationYahooUser() {
		new RosterEvent(new RosterV1(new MockFriendManager()), null, RosterEventType.add);
	}

	/**
	 * Argument 'type' of the constructor cannot be null. Test method for
	 * {@link org.openymsg.roster.RosterEvent#RosterEvent(org.openymsg.roster.Roster, org.openymsg.network.YahooUser, org.openymsg.roster.RosterEventType)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRosterEventArgumentValidationType() {
		new RosterEvent(new RosterV1(new MockFriendManager()), new YahooUserV1("dummy"), null);
	}

	/**
	 * Test method for {@link org.openymsg.roster.RosterEvent#getUser()}.
	 */
	@Test
	public void testGetUser() {
		final YahooUser user = new YahooUserV1("dummy");

		// should work for all RosterEventTypes
		for (final RosterEventType type : RosterEventType.values()) {
			assertEquals("failed on type=" + type, user, new RosterEvent(
					new RosterV1(new MockFriendManager()), user, type).getUser());
		}
	}

	/**
	 * Test method for {@link org.openymsg.roster.RosterEvent#getType()}.
	 */
	@Test
	public void testGetType() {
		for (final RosterEventType type : RosterEventType.values()) {
			assertEquals(type, new RosterEvent(new RosterV1(new MockFriendManager()), new YahooUserV1(
					"dummy"), type).getType());
		}
	}

	/**
	 * Test method for {@link org.openymsg.roster.RosterEvent#getSource()}.
	 */
	@Test
	public void testGetSource() {
		final RosterV1 roster = new RosterV1(new MockFriendManager());
		for (final RosterEventType type : RosterEventType.values()) {
			assertEquals(roster, new RosterEvent(roster,
					new YahooUserV1("dummy"), type).getSource());
		}
	}
}
