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
 * From Friend Friends Group friendsUpdateReceived y y y n friendAddedReceived y
 * y y y friendRemovedReceived y y y y
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionFriendEvent extends SessionEvent {
	protected YahooUser[] list;

	protected String group;

	/**
	 * CONSTRUCTORS
	 */
	public SessionFriendEvent(Object o, int sz) // Friends list update
	{
		super(o);
		list = new YahooUser[sz];
		group = null;
	}

	public SessionFriendEvent(Object o, YahooUser yu, String gp) // Friend
	// added
	{
		this(o, 1);
		setUser(0, yu);
		group = gp;
	}

	/**
	 * Accessors
	 */
	// Friends update
	public void setUser(int i, YahooUser yu) {
		list[i] = yu;
	}

	public YahooUser[] getFriends() {
		return list;
	}

	// Friend added
	public YahooUser getFriend() {
		return list[0];
	}

	public String getGroup() {
		return group;
	}

	@Override
	public String getFrom() {
		return list[0].getId();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (list.length > 1)
			sb.append(" list(size):").append(list.length);
		else
			sb.append(" friend:").append(list[0].getId()).append(" group:")
					.append(group);
		return sb.toString();
	}
}
