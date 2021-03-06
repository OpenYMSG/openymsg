package org.openymsg.legacy.roster;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.legacy.network.YahooUser;

import junitx.util.PrivateAccessor;

/**
 * Testcase to check if an 'syncedAdd()' to a {@link Roster} triggers the expected Event to be broadcasted to all
 * listeners.
 * <p>
 * Note that this method checks the event listener routines only. Other methods will check if the 'syncedAdd'
 * functionality actually manipulate the content of the Roster.
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class RosterSyncedAddMethodTriggersUpdate {
	private static final YahooUser USER = new YahooUser("dummy");
	private Roster roster;

	@Before
	public void setUp() {
		roster = new Roster(new MockFriendManager());
	}

	/**
	 * Checks if a broadcasted event is received by a RosterListener that has been added to the Roster.
	 * @throws Throwable
	 */
	@Test
	public void testReceiveOnOneRegisteredListener() throws Throwable {
		final MockRosterListener listener = new MockRosterListener();
		roster.addRosterListener(listener);
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] {YahooUser.class}, new Object[] {USER});
		assertEquals(1, listener.getEventCount());
	}

	/**
	 * Checks if a broadcasted event is received by all RosterListeners that have been added to the Roster.
	 * @throws Throwable
	 */
	@Test
	public void testReceiveOnTwoRegisteredListeners() throws Throwable {
		final MockRosterListener listenerOne = new MockRosterListener();
		final MockRosterListener listenerTwo = new MockRosterListener();
		roster.addRosterListener(listenerOne);
		roster.addRosterListener(listenerTwo);
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] {YahooUser.class}, new Object[] {USER});
		assertEquals(1, listenerOne.getEventCount());
		assertEquals(1, listenerTwo.getEventCount());
	}

	/**
	 * Checks if a broadcasted event is disregarded by a RosterListener that has not been added to the Roster.
	 * @throws Throwable
	 */
	@Test
	public void testDontReceiveOnUnregisteredListener() throws Throwable {
		final MockRosterListener listener = new MockRosterListener();
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] {YahooUser.class}, new Object[] {USER});
		assertEquals(0, listener.getEventCount());
	}

	/**
	 * Checks if an event gets recorded by the registered listener while it does not at an unregistered one.
	 * @throws Throwable
	 */
	@Test
	public void testCombinationOfRegisteredAndUnregisteredListeners() throws Throwable {
		final MockRosterListener listener = new MockRosterListener();
		final MockRosterListener nonlistener = new MockRosterListener();
		roster.addRosterListener(listener);
		PrivateAccessor.invoke(roster, "syncedAdd", new Class[] {YahooUser.class}, new Object[] {USER});
		assertEquals(1, listener.getEventCount());
		assertEquals(0, nonlistener.getEventCount());
	}
}
