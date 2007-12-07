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

import org.openymsg.network.YahooConference;
import org.openymsg.network.YahooUser;

/**
 * From To Message Room Users User conferenceInviteReceived y y y(top.) y y n
 * conferenceLogonReceived y y n y n y conferenceLogoffReceived y y n y n y
 * conferenceMessageReceived y y y y n y
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionConferenceEvent extends SessionEvent {
	private YahooConference room;

	private YahooUser[] users;

	public SessionConferenceEvent(Object o, String t, String f, String m,
			YahooConference r) {
		super(o, t, f, m);
		room = r;
	}

	public SessionConferenceEvent(Object o, String t, String f, String m,
			YahooConference r, YahooUser[] u) {
		this(o, t, f, m, r);
		users = u;
	}

	public YahooConference getRoom() {
		return room;
	}

	public YahooUser[] getUsers() {
		return users;
	}

	public YahooUser getUser() {
		return users[0];
	}

	public String getTopic() {
		return getMessage();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" room:").append(room);
		if (users != null)
			sb.append(" users(size):").append(users.length);
		return sb.toString();
	}
}
