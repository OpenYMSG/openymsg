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

import java.util.ArrayList;
import java.util.Collection;

import org.openymsg.network.YahooUser;

/**
 * From Friend Friends Group friendsUpdateReceived y y y n friendAddedReceived y
 * y y y friendRemovedReceived y y y y
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionFriendEvent extends SessionEvent {
	protected Collection<YahooUser> users = new ArrayList<YahooUser>();

	protected String group;

	/**
	 * CONSTRUCTORS
	 */
	public SessionFriendEvent(Object o) // Friends list update
	{
		super(o);
		group = null;
	}

	public SessionFriendEvent(Object o, YahooUser yu, String gp) // Friend
	// added
	{
		this(o);
		addUser(yu);
		group = gp;
	}

	/**
	 * Accessors
	 */
	// Friends update
	public void addUser(YahooUser yu) {
		users.add(yu);
	}

	public Collection<YahooUser> getUsers() {
		return users;
	}

	// Friend added
	public YahooUser getFirstUser() {
		return users.iterator().next();
	}
	
	public YahooUser getFriend(String id) {
		for (YahooUser user: getUsers()) {
			if(id.equals(user.getId()))
				return user;
		}
		return null;
	}

	public String getGroup() {
		return group;
	}

	@Override
	public String getFrom() {
		return getFirstUser().getId();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (users.size()> 1)
			sb.append(" list(size):").append(users.size());
		else
			sb.append(" friend:").append(getFirstUser().getId()).append(" group:")
					.append(group);
		return sb.toString();
	}
}
