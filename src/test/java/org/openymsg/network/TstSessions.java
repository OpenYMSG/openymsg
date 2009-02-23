package org.openymsg.network;

import java.io.IOException;

import org.openymsg.network.event.WaitListener;
import org.openymsg.roster.Roster;

/**
 * Left off the 'e' so that it won't be picked up for testing
 * @author neil hart
 *
 * @param <T> Roster
 * @param <U> YahooUser
 */
public abstract class TstSessions<T extends Roster<U>, U extends YahooUser> {
	public static String USERNAME = PropertiesAvailableTest.getUsername("presenceuser1");
	public static String PASSWORD = PropertiesAvailableTest.getPassword(USERNAME);
	public static String OTHERUSR = PropertiesAvailableTest.getUsername("logintestuser3");
	public static String OTHERPWD = PropertiesAvailableTest.getPassword(OTHERUSR);
	
	protected Session<T, U> sess1;
	protected Session<T, U> sess2;
	protected WaitListener listener1;
	protected WaitListener listener2;

	public TstSessions() throws Throwable {
		try {
			sess1 = createSession();
			sess2 = createSession();
			listener1 = new WaitListener(sess1);
			listener2 = new WaitListener(sess2);
			sess1.login(USERNAME, PASSWORD);
			sess2.login(OTHERUSR, OTHERPWD);
			sess1.addSessionListener(listener1);
			sess2.addSessionListener(listener2);
			listener1.waitForEvent(2, ServiceType.LOGON);
			listener2.waitForEvent(2, ServiceType.LOGON);
			removeAllContacts(sess1);
			removeAllContacts(sess2);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Release resources
	 * @throws Exception
	 */
	public void dispose() throws Exception {
		removeAllContacts(sess1);
		if (sess1.getSessionStatus() == SessionState.LOGGED_ON) {
			sess1.logout();
		}

		if (sess2.getSessionStatus() == SessionState.LOGGED_ON) {
			sess2.logout();
		}
	}

	protected abstract Session<T, U> createSession();
	

	/**
	 * Drains the event queue;
	 */
	protected void drain() {
		listener1.clearEvents();
		listener2.clearEvents();
	}

	/**
	 * Removes all contacts from the roster of the user that logged in using the
	 * specified session.
	 * 
	 * @throws IOException
	 */
	protected void removeAllContacts(Session<T, U> sess) {
		drain();

		final T roster = sess.getRoster();
		
		for (final YahooUser user : roster) {
			// TODO: Set#remove() in a for-each loop? :S
			roster.remove(user);
			listener1.waitForEvent(5, ServiceType.FRIENDREMOVE);
		}
	}
	public Session<T, U> getSess1() {
		return sess1;
	}

	public Session<T, U> getSess2() {
		return sess2;
	}

	public WaitListener getListener1() {
		return listener1;
	}

	public WaitListener getListener2() {
		return listener2;
	}


}
