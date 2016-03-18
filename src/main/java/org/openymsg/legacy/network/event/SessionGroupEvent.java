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

/**
 * Group rename event.
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionGroupEvent extends SessionEvent {
	protected String oldName, newName;

	/**
	 * CONSTRUCTORS
	 */
	public SessionGroupEvent(Object o, String on, String nn) // Group rename
	{
		super(o);
		oldName = on;
		newName = nn;
	}

	/**
	 * Accessors
	 */
	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" oldname:").append(oldName).append(" newname:").append(newName);
		return sb.toString();
	}
}
