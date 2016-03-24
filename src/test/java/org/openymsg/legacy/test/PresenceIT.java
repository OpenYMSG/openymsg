/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol. Copyright (C) 2007 G. der Kinderen,
 * Nimbuzz.com This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openymsg.legacy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openymsg.SlowTest;
import org.openymsg.legacy.network.ServiceType;
import org.openymsg.legacy.network.Session;
import org.openymsg.legacy.network.Status;
import org.openymsg.legacy.network.YahooProtocol;
import org.openymsg.legacy.network.YahooUser;
import org.openymsg.legacy.network.event.SessionAdapter;
import org.openymsg.legacy.network.event.SessionFriendEvent;
import org.openymsg.legacy.network.event.WaitListener;
import org.openymsg.legacy.roster.Roster;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class PresenceIT {
	private static String USERNAME = PropertiesAvailableTest.getUsername("presenceuser1");
	private static String PASSWORD = PropertiesAvailableTest.getPassword(USERNAME);
	private static String RECEIVER = PropertiesAvailableTest.getUsername("presenceuser2");
	private static String RECVPWD = PropertiesAvailableTest.getPassword(RECEIVER);

	// @Test
	public void testSetInitalStatus() throws Exception {
		// test initial state: set your state before logging in.
		for (final Status status : Status.values()) {
			final Session session = new Session();
			if (status == Status.AVAILABLE || status == Status.INVISIBLE) {
				try {
					session.setStatus(status);
				} catch (IllegalArgumentException e) {
					fail("This operation should not have thrown an IllegalArgumentException using status " + status);
				}
				assertEquals(status, session.getStatus());
			} else {
				try {
					session.setStatus(status);
					fail("This operation should have thrown an IllegalArgument using status " + status);
				} catch (IllegalArgumentException e) {
					// should happen.
				}
			}
		}
	}

	// @Test
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
			fail("This operation should have thrown an IllegalArgument using status " + Status.NOTATDESK);
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
	@Category(SlowTest.class)
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

	@Test
	@Category(SlowTest.class)
	public void testReceivePresence() throws Exception {
		final Session sender = new Session();
		final Session receiver = new Session();
		final ReceivePresenceUpdateAdaptor listener = new ReceivePresenceUpdateAdaptor();
		WaitListener wl = new WaitListener(receiver);
		receiver.addSessionListener(listener);
		try {
			receiver.login(RECEIVER, RECVPWD);
			sender.login(USERNAME, PASSWORD);
			assertEquals(Status.AVAILABLE, receiver.getStatus());
			assertEquals(null, receiver.getCustomStatusMessage());
			final Roster roster = receiver.getRoster();
			final boolean exists = roster.containsUser(RECEIVER);
			if (!exists) {
				roster.add(new YahooUser(USERNAME, "Friends", YahooProtocol.YAHOO));
				wl.waitForEvent(4, ServiceType.FRIENDADD);
			}
			sender.setStatus(Status.BUSY);
			SessionFriendEvent event = (SessionFriendEvent) wl.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE).getEvent();
			assertNotNull(event);
			assertEquals(event.getUser().toString(), Status.BUSY, event.getUser().getStatus());
			assertEquals(null, event.getUser().getCustomStatusMessage());
			Thread.sleep(1000);
			sender.setStatus(Status.AVAILABLE);
			event = (SessionFriendEvent) wl.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE).getEvent();
			assertNotNull(event);
			assertEquals(Status.AVAILABLE, event.getUser().getStatus());
			assertEquals(null, event.getUser().getCustomStatusMessage());
			Thread.sleep(1000);
			sender.setStatus(Status.OUTTOLUNCH);
			event = (SessionFriendEvent) wl.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE).getEvent();
			assertNotNull(event);
			assertEquals(Status.OUTTOLUNCH, event.getUser().getStatus());
			assertEquals(null, event.getUser().getCustomStatusMessage());
			Thread.sleep(1000);
			sender.setStatus("This is my custom message!", false);
			event = (SessionFriendEvent) wl.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE).getEvent();
			assertNotNull(event);
			assertEquals(Status.CUSTOM, event.getUser().getStatus());
			assertEquals("This is my custom message!", event.getUser().getCustomStatusMessage());
			Thread.sleep(1000);
			sender.setStatus("This is my busy custom message!", false);
			event = (SessionFriendEvent) wl.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE).getEvent();
			assertNotNull(event);
			assertEquals(Status.CUSTOM, event.getUser().getStatus());
			assertEquals("This is my busy custom message!", event.getUser().getCustomStatusMessage());
			Thread.sleep(1000);
			sender.setStatus(Status.OUTTOLUNCH);
			event = (SessionFriendEvent) wl.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE).getEvent();
			assertNotNull(event);
			assertEquals(Status.OUTTOLUNCH, event.getUser().getStatus());
			assertEquals(null, event.getUser().getCustomStatusMessage());
		} finally {
			try {
				sender.logout();
			} catch (Exception e) {
				// ignore
			}
			try {
				receiver.logout();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * Utility class that lets you wait for a particular event.
	 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
	 */
	private static class ReceivePresenceUpdateAdaptor extends SessionAdapter {
		/**
		 * The queued event.
		 */
		private SessionFriendEvent sessionEvent = null;

		public ReceivePresenceUpdateAdaptor() {
			// doesn't do much, but prevens synthetic accessor warnings.
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.openymsg.legacy.network.event.SessionAdapter#friendsUpdateReceived(org.openymsg.legacy.network.event.
		 * SessionFriendEvent )
		 */
		@Override
		public void friendsUpdateReceived(SessionFriendEvent event) {
			this.sessionEvent = event;
		}

		/**
		 * Waits for a particular amount of time for the next event to arrive, and then returns that event. If within
		 * the timeout no event has been received, this method returns null.
		 * @param timeout_in_millis The time (in milliseconds) to wait for the next event.
		 * @return 'null' or the first event that arrived within the timeout value.
		 */
		public SessionFriendEvent waitForNextEvent(long timeout_in_millis) {
			if (timeout_in_millis < 0) {
				throw new IllegalArgumentException("Cannot use negative timeout values.");
			}
			if (sessionEvent != null) {
				throw new IllegalStateException("There's already an event queued, before this method was called.");
			}
			final long STEP_IN_MILLIS = 100;
			SessionFriendEvent result = null;
			for (int i = 0; i < timeout_in_millis; i += STEP_IN_MILLIS) {
				if (sessionEvent != null) {
					result = sessionEvent;
					break;
				}
				try {
					Thread.sleep(STEP_IN_MILLIS);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			sessionEvent = null;
			return result;
		}
	}
}
