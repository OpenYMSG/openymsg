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
package org.openymsg.network;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single group of friend users in a Yahoo friends list.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
/*
 * TODO: manipulating the group should also cause relevant packets to be sent to
 * the network.
 */
public class YahooGroup {
	private String name;

	private boolean isOpen;

	private Set<YahooUser> users;

	/**
	 * Creates a new YahooGroup instance.
	 * 
	 * @param name
	 *            Name of the group.
	 * @param isOpen
	 *            ''true'' the create an open group, ''false'' otherwise.
	 */
	public YahooGroup(String name, boolean isOpen) {
		this.name = name;
		this.isOpen = isOpen;
		users = new HashSet<YahooUser>();
	}

	/**
	 * Creates a new, open group.
	 * 
	 * @param name
	 *            Name of the group.
	 */
	public YahooGroup(String name) {
		this(name, true);
	}

	public void addUser(YahooUser yu) {
		users.add(yu);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String nm) {
		name = nm;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Set<YahooUser> getUsers() {
		return users;
	}

	@Override
	public String toString() {
		return "YahooGroup [name=" + name + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof YahooGroup)
			return ((YahooGroup) other).getName().equals(this.getName());
		else return super.equals(other);
	}


}
