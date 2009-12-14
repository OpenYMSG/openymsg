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

import java.util.Date;

/**
 * This class is the parent of all event classes in this package.
 * 
 * To From Message Timestamp contactRejectionReceived y y y n
 * contactRequestReceived y y y y messageReceived y y y n buzzReceived y y y n
 * offlineMessageReceived y y y y listReceived n n n n logoffReceived n n n n
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionEvent extends java.util.EventObject {
	private static final long serialVersionUID = -128672051097761320L;

	private final String to;

	private final String from;

	protected String message;

	private final long timestamp;

	private long status = 0;

	public SessionEvent(Object source) {
		this(source, null, null, null, 0);
	}

	// Online message
	public SessionEvent(Object source, String to, String from, String message) {
		this(source, to, from, message, 0);
	}

	// Offline message
	public SessionEvent(Object source, String to, String from, String message,
			long timestampInMillis) {
		super(source);
		this.to = to;
		this.from = from;
		this.message = message;
		this.timestamp = timestampInMillis;
	}

	/**
	 * Accessors
	 */
	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}

	public Date getTimestamp() {
		return new Date(timestamp);
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long s) {
		status = s;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getCanonicalName()).append(" to:").append(to).append(" from:")
				.append(from).append(" message:").append(message).append(
						" timestamp:").append(timestamp);
		return sb.toString();
	}
}
