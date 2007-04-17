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

	protected int mode;

	/**
	 * CONSTRUCTOR
	 */
	public SessionNotifyEvent(Object o, String t, String f, String m,
			String ty, String md) {
		super(o, t, f, m);
		type = ty;
		mode = Integer.parseInt(md);
	}

	public int getMode() {
		return mode;
	}

	public String getType() {
		return type;
	}

	public String getGame() {
		return getMessage();
	}

	public boolean isTyping() {
		return (type != null && type
				.equalsIgnoreCase(StatusConstants.NOTIFY_TYPING));
	}

	public boolean isGame() {
		return (type != null && type
				.equalsIgnoreCase(StatusConstants.NOTIFY_GAME));
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString()).append(" type:")
				.append(type).append(" mode:").append(mode);
		return sb.toString();
	}
}
