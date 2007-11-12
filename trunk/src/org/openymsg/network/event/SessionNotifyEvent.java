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
package org.openymsg.network.event;

import org.openymsg.network.StatusConstants;

/**
 * This event is used to convey Yahoo notification events, like typing on/off
 * from other Yahoo users we're communicating with.
 * 
 * To From Message Type Mode notifyReceived y y y y y
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionNotifyEvent extends SessionEvent {

	protected String type;

	protected boolean mode;

	/**
	 * Factory method to create an instance based on raw input types.
	 */
	public static SessionNotifyEvent createSessionNotifyEvent(Object source,
			String to, String from, String message, String type, String mode) {
		final boolean m;
		if ("0".equals(mode)) {
			m = false;
		} else if ("1".equals(mode)) {
			m = true;
		} else {
			throw new IllegalArgumentException(
					"A mode of '0' or '1' is expected. The provide mode was: "
							+ mode);
		}

		return new SessionNotifyEvent(source, to, from, message, type, m);
	}

	/**
	 * Creates a new event that represents a notification.
	 * 
	 * @param source
	 *            {@link SessionEvent#getSource()}
	 * @param to
	 *            {@link SessionEvent#getTo()}
	 * @param from
	 *            {@link SessionEvent#getFrom()}
	 * @param message
	 *            May contain the game name for some types of notifications.
	 * @param type
	 *            The type of the notification
	 * @param mode
	 *            boolean value that tells us if this is an ON or OFF
	 *            notification.
	 */
	public SessionNotifyEvent(Object source, String to, String from,
			String message, String type, boolean mode) {
		super(source, to, from, message);
		this.type = type;
		this.mode = mode;
	}

	/**
	 * Returns the mode of this typing notification event: true for 'ON' or
	 * false for 'OFF'.
	 * 
	 * @return the typing notification mode.
	 */
	public boolean getMode() {
		return mode;
	}

	/**
	 * Convenience method to check if this is an event telling us that the user
	 * is currently is setting an 'active' event. This corresponds to a Mode
	 * that is set to 'ON'.
	 * 
	 * @return ''true'' if the Mode for this event is 'ON', false otherwise.
	 */
	public boolean isOn() {
		return mode;
	}

	/**
	 * Convenience method to check if this is an event telling us that the user
	 * is currently is canceling an 'active' event. This corresponds to a Mode
	 * that is set to 'OFF'.
	 * 
	 * @return ''true'' if the Mode for this event is 'OFF', false otherwise.
	 */
	public boolean isOff() {
		return !mode;
	}

	/**
	 * Returns the type of this notification. This identifier can identify the
	 * activity for which the event was fired. Examples are 'TYPING' or 'GAME'.
	 * 
	 * @return The type of action for which this notification was sent.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the game that is being played. Note that this method will throw a
	 * runtime exception if no game is played.
	 * 
	 * @return Identifier of the game for which a notification is sent.
	 * @throws IllegalStateException
	 *             If the type of this notification does not equal the type for
	 *             gaming.
	 */
	public String getGame() {
		if (!isGame()) {
			throw new IllegalStateException(
					"This is not a gaming-typed notification.");
		}
		return getMessage();
	}

	/**
	 * Checks if this notification regards a 'TYPING' event.
	 * 
	 * @return ''true'' if this notification is a 'TYPING' event, ''false''
	 *         otherwise.
	 */
	public boolean isTyping() {
		return (type != null && type
				.equalsIgnoreCase(StatusConstants.NOTIFY_TYPING));
	}

	/**
	 * Checks if this notification regards a 'GAME' event.
	 * 
	 * @return ''true'' if this notification is a 'GAME' event, ''false''
	 *         otherwise.
	 */
	public boolean isGame() {
		return (type != null && type
				.equalsIgnoreCase(StatusConstants.NOTIFY_GAME));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openymsg.network.event.SessionEvent#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString()).append(" type:")
				.append(type).append(" mode:").append((mode ? "1" : "0"));
		return sb.toString();
	}
}
