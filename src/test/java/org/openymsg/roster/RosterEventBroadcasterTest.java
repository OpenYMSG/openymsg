package org.openymsg.roster;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.network.YahooUser;
import org.openymsg.v1.network.YahooUserV1;
import org.openymsg.v1.roster.RosterV1;

/**
 * Tests to check the functionality of the mechanism that should broadcast any
 * given {@link RosterEvent} to all registered Listeners. This testcase tests
 * the broadcast-method itself, rather than checking if add() and remove()
 * functions trigger a broadcast. For that, refer to other testcases like
 * {@link RosterSyncedAddMethodTriggersUpdate}.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterEventBroadcasterTest {

	private RosterV1 roster;

	@Before
	public void setUp() {
		roster = new RosterV1(new MockFriendManager());
	}

	/**
	 * Checks if a broadcasted event is received by a RosterListener that has
	 * been added to the Roster.
	 */
	@Test
	public void testReceiveOnOneRegisteredListener() {
		final MockRosterListener listener = new MockRosterListener();
		roster.addRosterListener(listener);
		roster.broadcastEvent(new RosterEvent(roster, new YahooUserV1("dummy"),
				RosterEventType.add));
		assertEquals(1, listener.getEventCount());
	}

	/**
	 * Checks if a broadcasted event is received by all RosterListeners that
	 * have been added to the Roster.
	 */
	@Test
	public void testReceiveOnTwoRegisteredListeners() {
		final MockRosterListener listenerOne = new MockRosterListener();
		final MockRosterListener listenerTwo = new MockRosterListener();
		roster.addRosterListener(listenerOne);
		roster.addRosterListener(listenerTwo);
		roster.broadcastEvent(new RosterEvent(roster, new YahooUserV1("dummy"),
				RosterEventType.add));
		assertEquals(1, listenerOne.getEventCount());
		assertEquals(1, listenerTwo.getEventCount());
	}

	/**
	 * Checks if a broadcasted event is disregarded by a RosterListener that has
	 * not been added to the Roster.
	 */
	@Test
	public void testDontReceiveOnUnregisteredListener() {
		final MockRosterListener listener = new MockRosterListener();
		roster.broadcastEvent(new RosterEvent(roster, new YahooUserV1("dummy"),
				RosterEventType.add));
		assertEquals(0, listener.getEventCount());
	}

	/**
	 * Checks if an event gets recorded by the registered listener while it does
	 * not at an unregistered one.
	 */
	@Test
	public void testCombinationOfRegisteredAndUnregisteredListeners() {
		final MockRosterListener listener = new MockRosterListener();
		final MockRosterListener nonlistener = new MockRosterListener();
		roster.addRosterListener(listener);
		roster.broadcastEvent(new RosterEvent(roster, new YahooUserV1("dummy"),
				RosterEventType.add));
		assertEquals(1, listener.getEventCount());
		assertEquals(0, nonlistener.getEventCount());
	}
}
