/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.network;

import static junit.framework.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Test;
import org.openymsg.network.LoginRefusedException;
import org.openymsg.network.ServiceType;
import org.openymsg.network.Session;
import org.openymsg.network.SessionState;
import org.openymsg.network.YahooException;
import org.openymsg.network.event.WaitListener;
import org.openymsg.v1.network.SessionV1;
import org.openymsg.v1.roster.RosterV1;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class LoginTest {
	private static String USERNAME = PropertiesAvailableTest
			.getUsername("logintestuser1");

	private static String PASSWORD = PropertiesAvailableTest
			.getPassword(USERNAME);

	private static String OTHERUSR = PropertiesAvailableTest
			.getUsername("logintestuser2");

	private static String OTHERPWD = PropertiesAvailableTest
			.getPassword(OTHERUSR);

	protected static Session<RosterV1> createSession() {
		return new SessionV1();
	}


	@Test
	public void testFalseLogin() throws Exception {
		Session<RosterV1> session = createSession();
		assertEquals(SessionState.UNSTARTED, session.getSessionStatus());
		try {
			session.login("sdfds", null);
			Assert
					.fail("An IllegalArgumentException should have been thrown after the #login() call.");
		} catch (IllegalArgumentException e) {
			// should happen.
		}
		assertEquals(SessionState.FAILED, session.getSessionStatus());

		session = createSession();
		assertEquals(SessionState.UNSTARTED, session.getSessionStatus());

		try {
			session.login(null, "sdfds");
			Assert
					.fail("An IllegalArgumentException should have been thrown after the #login() call.");
		} catch (IllegalArgumentException e) {
			// should happen.
		}

		session = createSession();
		try {
			session.login("sdfsda", "");
			Assert
					.fail("An IllegalArgumentException should have been thrown after the #login() call.");
		} catch (IllegalArgumentException e) {
			// should happen.
		}

		session = createSession();
		try {
			session.login("", "sdfds");
			Assert
					.fail("An IllegalArgumentException should have been thrown after the #login() call.");
		} catch (IllegalArgumentException e) {
			// should happen.
		}
		assertEquals(SessionState.FAILED, session.getSessionStatus());

	}

	@Test
	public void testLogin() throws Exception {
		final Session<RosterV1> session = createSession();
		assertEquals(SessionState.UNSTARTED, session.getSessionStatus());
		try {
			session.login(USERNAME, PASSWORD);
		} catch (YahooException ex) {
			ex.printStackTrace();
			Assert.fail("This should not have thrown an exception, but did: "
					+ ex.getMessage());
		}
		assertEquals(SessionState.LOGGED_ON, session.getSessionStatus());
		session.logout();
	}

	// @Test // according to Jive, this should be possible, but it isn't?
	public void testLoginWithUsernameAsEmail() throws Exception {
		final Session<RosterV1> session = createSession();
		try {
			session.login(USERNAME + "@yahoo.com", PASSWORD);
		} catch (Exception e) {
			// Should NOT happen!
			e.printStackTrace();
			Assert
					.fail("An unexpected exception was caught: "
							+ e.getMessage());
		}
		assertEquals(SessionState.LOGGED_ON, session.getSessionStatus());
		session.logout();
	}

	@Test
	public void testLoginIncorrectPassword() throws Exception {
		final Session<RosterV1> session = createSession();
		try {
			session.login(USERNAME, "incorrect!");
			session.logout();
			Assert
					.fail("A LoginRefusedException should have been thrown after the #login() call.");
		} catch (LoginRefusedException e) {
			// should happen.
			assertEquals(SessionState.FAILED, session.getSessionStatus());
		}
	}

	@Test
	public void testDuplicateLogin() throws Exception {
		// first session should login ok, the second session shouldn't.
		final Session<RosterV1> sessionOne = createSession();
		final Session<RosterV1> sessionTwo = createSession();
		sessionOne.login(USERNAME, PASSWORD);

		sessionTwo.login(USERNAME, PASSWORD);
		sessionTwo.logout();
		try {
			sessionOne.logout();
			Assert
					.fail("An IllegalStateException should have been thrown by now.");
		} catch (IllegalStateException e) {
			// should happen
		}
		assertEquals(SessionState.UNSTARTED, sessionOne.getSessionStatus());
		assertEquals(SessionState.UNSTARTED, sessionTwo.getSessionStatus());

	}

	@Test
	public void testLoginTwiceOnSameSession() throws Exception {
		final Session<RosterV1> session = createSession();
		session.login(USERNAME, PASSWORD);
		try {
			session.login(USERNAME, PASSWORD);
			Assert
					.fail("An IllegalStateException should have been thrown by now.");
		} catch (IllegalStateException e) {
			// should happen
		} finally {
			session.logout();
		}
	}

	@Test
	public void testLoginLogoutAndLoginAgain() throws Exception {
		// using two session objects
		final Session<RosterV1> sessionOne = createSession();
		sessionOne.login(USERNAME, PASSWORD);
		assertEquals(SessionState.LOGGED_ON, sessionOne.getSessionStatus());
		sessionOne.logout();
		assertEquals(SessionState.UNSTARTED, sessionOne.getSessionStatus());

		final Session<RosterV1> sessionTwo = createSession();
		sessionTwo.login(USERNAME, PASSWORD);
		assertEquals(SessionState.LOGGED_ON, sessionTwo.getSessionStatus());
		sessionTwo.logout();
		assertEquals(SessionState.UNSTARTED, sessionTwo.getSessionStatus());
	}

	@Test
	public void testLoginLogoutAndLoginAgainUsingTheSameSessionObject()
			throws Exception {
		final Session<RosterV1> session = createSession();
		WaitListener wl = new WaitListener(session);
		assertEquals(SessionState.UNSTARTED, session.getSessionStatus());
		session.login(USERNAME, PASSWORD);
		wl.waitForEvent(5, ServiceType.LOGON);
		assertEquals(SessionState.LOGGED_ON, session.getSessionStatus());
		session.logout();
		Thread.sleep(500);
		assertEquals(SessionState.UNSTARTED, session.getSessionStatus());
		session.login(USERNAME, PASSWORD);
		wl.waitForEvent(5, ServiceType.LOGON);
		assertEquals(SessionState.LOGGED_ON, session.getSessionStatus());
		session.logout();
		assertEquals(SessionState.UNSTARTED, session.getSessionStatus());
	}

	@Test
	public void testDuplicateLogins() throws Exception {
		final Session<RosterV1> sessionOne = createSession();
		final Session<RosterV1> sessionTwo = createSession();
		assertEquals(SessionState.UNSTARTED, sessionOne.getSessionStatus());
		assertEquals(SessionState.UNSTARTED, sessionTwo.getSessionStatus());
		sessionOne.login(USERNAME, PASSWORD);
		sessionTwo.login(OTHERUSR, OTHERPWD);
		assertEquals(SessionState.LOGGED_ON, sessionOne.getSessionStatus());
		assertEquals(SessionState.LOGGED_ON, sessionTwo.getSessionStatus());
		sessionOne.logout();
		sessionTwo.logout();
		assertEquals(SessionState.UNSTARTED, sessionOne.getSessionStatus());
		assertEquals(SessionState.UNSTARTED, sessionTwo.getSessionStatus());
	}
}
