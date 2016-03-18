/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol. Copyright (C) 2007 G. der Kinderen,
 * Nimbuzz.com This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openymsg.legacy.network.event;

import org.openymsg.legacy.network.YahooConference;
import org.openymsg.legacy.network.YahooUser;

import java.util.Set;

public class SessionConferenceInviteEvent extends AbstractSessionConferenceEvent {
	private static final long serialVersionUID = 6550109967442109240L;
	private Set<YahooUser> invitedUsers;
	private Set<YahooUser> currentUsers;

	public SessionConferenceInviteEvent(Object o, String to, String from, String message, YahooConference conference,
			Set<YahooUser> invitedUsers, Set<YahooUser> currentUsers) {
		super(o, to, from, message, conference);
		this.invitedUsers = invitedUsers;
		this.currentUsers = currentUsers;
	}

	public Set<YahooUser> getInvitedUsers() {
		return invitedUsers;
	}

	public Set<YahooUser> getCurrentUsers() {
		return currentUsers;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (invitedUsers != null)
			sb.append(" invitedUsers(size):").append(invitedUsers.size());
		if (currentUsers != null)
			sb.append(" currentUsers(size):").append(currentUsers.size());
		return sb.toString();
	}
}
