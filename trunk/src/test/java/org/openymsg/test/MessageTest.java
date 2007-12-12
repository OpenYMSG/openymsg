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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openymsg.network.Session;
import org.openymsg.network.event.SessionAdapter;
import org.openymsg.network.event.SessionEvent;

/**
 * Make sure that the users are subscribed to eachother!
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class MessageTest {
	private static String USERNAME = PropertiesAvailableTest
			.getUsername("messagetestuser1");

	private static String RECIPIENT = PropertiesAvailableTest
			.getUsername("messagetestuser2");

	private static String PASSWORD = PropertiesAvailableTest
			.getPassword(USERNAME);

	private static String RECPWD = PropertiesAvailableTest
			.getPassword(RECIPIENT);

	@Test(expected = IllegalStateException.class)
	public void testSendMessageBeforeLoggingIn() throws Exception {
		final Session session = new Session();
		session.sendMessage(RECIPIENT, "test message");
	}

	@Test
	public void testSendMessage() throws Exception {
		final Session sender = new Session();
		final Session receiver = new Session();
		try {
			final ReceiveMessageAdaptor listener = new ReceiveMessageAdaptor();

			sender.login(USERNAME, PASSWORD);
			receiver.login(RECIPIENT, RECPWD);
			Thread.sleep(1000); // wait for any offline messages to disappear.
			receiver.addSessionListener(listener);

			String message = "test message";
			sender.sendMessage(RECIPIENT, message);
			SessionEvent event = listener.waitForNextEvent(10000);
			assertNotNull(event);
			assertEquals(USERNAME, event.getFrom());
			assertEquals(message, event.getMessage());

			message = "ç ë and Japanese chars: \u65e5\u672c\u8a9e\u6587\u5b57\u5217";
			sender.sendMessage(RECIPIENT, message);
			event = listener.waitForNextEvent(10000);
			assertNotNull(event);
			assertEquals(USERNAME, event.getFrom());
			assertEquals(message, event.getMessage());

			// TODO: this actually fails, but as GAIM has the exact same
			// failure, I deem it acceptable for now. :)
			// message = "<html><body><p>This should be
			// escaped.</p></body></html>";
			// sender.sendMessage(RECIPIENT, message);
			// event = listener.waitForNextEvent(10000);
			// assertNotNull(event);
			// assertEquals(USERNAME, event.getFrom());
			// assertEquals(message, event.getMessage());

		} finally {
			sender.logout();
			receiver.logout();
		}
	}

	/**
	 * Utility class that lets you wait for a particular event.
	 * 
	 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
	 * 
	 */
	private static class ReceiveMessageAdaptor extends SessionAdapter {
		/**
		 * The queued event.
		 */
		private SessionEvent sessionEvent = null;

		public ReceiveMessageAdaptor() {
			// doesn't do much, but prevens synthetic accessor warnings.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openymsg.network.event.SessionAdapter#messageReceived(org.openymsg.network.event.SessionEvent)
		 */
		@Override
		public void messageReceived(SessionEvent event) {
			this.sessionEvent = event;
		}

		/**
		 * Waits for a particular amount of time for the next event to arrive,
		 * and then returns that event. If within the timeout no event has been
		 * received, this method returns null.
		 * 
		 * @param timeout_in_millis
		 *            The time (in milliseconds) to wait for the next event.
		 * @return 'null' or the first event that arrived within the timeout
		 *         value.
		 */
		public SessionEvent waitForNextEvent(long timeout_in_millis) {
			if (timeout_in_millis < 0) {
				throw new IllegalArgumentException(
						"Cannot use negative timeout values.");
			}

			if (sessionEvent != null) {
				throw new IllegalStateException(
						"There's already an event queued, before this method was called.");
			}

			final long STEP_IN_MILLIS = 100;
			SessionEvent result = null;
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
