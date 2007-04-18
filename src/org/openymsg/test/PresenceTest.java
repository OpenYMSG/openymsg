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
package org.openymsg.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openymsg.network.Session;
import org.openymsg.network.Status;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class PresenceTest {

	private static String USERNAME = PropertiesAvailableTest
			.getUsername("presenceuser1");

	private static String PASSWORD = PropertiesAvailableTest
			.getPassword(USERNAME);

	@Test
	public void testDefaultStatus() throws Exception {
		assertEquals(Status.AVAILABLE, new Session().getStatus());
	}

	@Test
	public void testSetInitalStatus() throws Exception {
		// test initial state: set your state before logging in.
		for (final Status status : Status.values()) {
			final Session session = new Session();
			if (status == Status.AVAILABLE || status == Status.INVISIBLE) {
				try {
					session.setStatus(status);
				} catch (IllegalArgumentException e) {
					fail("This operation should not have thrown an IllegalArgumentException using status "
							+ status);
				}
				assertEquals(status, session.getStatus());
			} else {
				try {
					session.setStatus(status);
					fail("This operation should have thrown an IllegalArgument using status "
							+ status);
				} catch (IllegalArgumentException e) {
					// should happen.
				}
			}
		}
	}

	@Test
	public void testReSetInitalStatus() throws Exception {
		final Session session = new Session();
		try {
			session.setStatus(Status.AVAILABLE);
			assertEquals(Status.AVAILABLE, session.getStatus());
			session.setStatus(Status.INVISIBLE);
			assertEquals(Status.INVISIBLE, session.getStatus());
			session.setStatus(Status.AVAILABLE);
			assertEquals(Status.AVAILABLE, session.getStatus());
			session.setStatus(Status.INVISIBLE);
			assertEquals(Status.INVISIBLE, session.getStatus());
		} catch (IllegalArgumentException e) {
			fail("This operation should not have thrown an IllegalArgumentException.");
		}

		try {
			session.setStatus(Status.NOTATDESK);
			fail("This operation should have thrown an IllegalArgument using status "
					+ Status.NOTATDESK);
		} catch (IllegalArgumentException e) {
			// should happen.
		}

		// resetting errors should result in the original status to be
		// available.
		assertEquals(Status.INVISIBLE, session.getStatus());

		// resetting after errors should be possible.
		session.setStatus(Status.AVAILABLE);
		assertEquals(Status.AVAILABLE, session.getStatus());
	}

	@Test
	public void testSetCustomInitialStatus() throws Exception {
		try {
			final Session session = new Session();
			session.setStatus(null, true);
			fail("An IllegalArgumentException should have been thrown before this point.");
		} catch (IllegalArgumentException e) {
			// should happen
		}

		try {
			final Session session = new Session();
			session.setStatus(null, false);
			fail("An IllegalArgumentException should have been thrown before this point.");
		} catch (IllegalArgumentException e) {
			// should happen
		}

		try {
			final Session session = new Session();
			session.setStatus("", true);
			fail("An IllegalArgumentException should have been thrown before this point.");
		} catch (IllegalArgumentException e) {
			// should happen
		}

		try {
			final Session session = new Session();
			session.setStatus("", false);
			fail("An IllegalArgumentException should have been thrown before this point.");
		} catch (IllegalArgumentException e) {
			// should happen
		}

		try {
			final Session session = new Session();
			session.setStatus("test", true);
			fail("An IllegalArgumentException should have been thrown before this point.");
		} catch (IllegalArgumentException e) {
			// should happen
		}

		try {
			final Session session = new Session();
			session.setStatus("test", false);
			fail("An IllegalArgumentException should have been thrown before this point.");
		} catch (IllegalArgumentException e) {
			// should happen
		}
	}

	@Test
	public void testActualLogin() throws Exception {
		final Session sender = new Session();
		try {
			sender.login(USERNAME, PASSWORD);

			sender.setStatus(Status.BUSY);
			Thread.sleep(1000);
			sender.setStatus(Status.AVAILABLE);
			Thread.sleep(1000);
			sender.setStatus(Status.OUTTOLUNCH);
			Thread.sleep(1000);
			sender.setStatus(Status.OUTTOLUNCH);
			Thread.sleep(1000);
			sender.setStatus(Status.AVAILABLE);
			Thread.sleep(1000);
			sender.setStatus("This is my custom message!", false);
			Thread.sleep(1000);
			sender.setStatus("This is my busy custom message!", true);
			Thread.sleep(1000);

		} finally {
			try {
				sender.logout();
			} catch (Exception e) {
				// ignore
			}
		}
	}
}
