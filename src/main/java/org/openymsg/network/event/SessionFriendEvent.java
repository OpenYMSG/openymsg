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

import org.openymsg.network.YahooUser;

/**
 * Represents an event triggered by a change in the presence or roster change of
 * a session. This Event typically gets thrown after a friend has been added,
 * removed or updated.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionFriendEvent extends SessionEvent {
	protected final YahooUser user;

	protected final String group;

	/**
	 * CONSTRUCTORS
	 */
	public SessionFriendEvent(Object o) // Friends list update
	{
		this(o, null, null);
	}

	public SessionFriendEvent(Object o, YahooUser yu, String gp) // Friend
	// added
	{
		super(o);
		user = yu;
		group = gp;
	}

	public YahooUser getUser() {
		return user;
	}

	public String getGroup() {
		return group;
	}

	@Override
	public String getFrom() {
		return user.getId();
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" friend:").append(getUser().getId()).append(" group:")
				.append(group);
		return sb.toString();
	}
}
