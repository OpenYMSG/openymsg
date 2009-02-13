/**
 * 
 */
package org.openymsg.network;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openymsg.network.ServiceType;
import org.openymsg.network.Session;
import org.openymsg.network.SessionState;
import org.openymsg.network.YahooUser;
import org.openymsg.network.event.WaitListener;
import org.openymsg.v1.network.SessionV1;
import org.openymsg.v1.roster.RosterV1;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 * 
 */
public class YahooTestAbstract {

	protected static final String CHATMESSAGE = "CHATMESSAGE";

	protected static final String USERNAME = PropertiesAvailableTest
			.getUsername("presenceuser1");

	protected static final String PASSWORD = PropertiesAvailableTest
			.getPassword(USERNAME);

	protected static final String OTHERUSR = PropertiesAvailableTest
			.getUsername("logintestuser3");

	protected static final String OTHERPWD = PropertiesAvailableTest
			.getPassword(OTHERUSR);
	
	static Session<RosterV1> sess1;
	static Session<RosterV1> sess2;
	static WaitListener listener1;
	static WaitListener listener2;

	/**
	 * @throws Throwable
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Throwable {
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

	protected static Session<RosterV1> createSession() {
		return new SessionV1();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		removeAllContacts(sess1);
		if (sess1.getSessionStatus() == SessionState.LOGGED_ON) {
			sess1.logout();
		}

		if (sess2.getSessionStatus() == SessionState.LOGGED_ON) {
			sess2.logout();
		}
	}

	/**
	 * Removes all contacts from the roster of the user that logged in using the
	 * specified session.
	 * 
	 * @throws IOException
	 */
	protected static void removeAllContacts(Session<RosterV1> sess) {
		drain();

		final RosterV1 roster = sess.getRoster();
		
		for (final YahooUser user : roster) {
			// TODO: Set#remove() in a for-each loop? :S
			roster.remove(user);
			listener1.waitForEvent(5, ServiceType.FRIENDREMOVE);
		}
	}

	/**
	 * Drains the event queue;
	 */
	protected static void drain() {
		listener1.clearEvents();
		listener2.clearEvents();
	}

}
