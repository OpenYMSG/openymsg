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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * To From Message Timestamp Location fileTransferReceived y y y y y
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionFileTransferEvent extends SessionEvent {
	protected URL location = null;

	/**
	 * CONSTRUCTORS
	 * 
	 * @throws MalformedURLException
	 */
	public SessionFileTransferEvent(Object object, String to, String from,
			String message, long timestampInMillis, String location)
			throws MalformedURLException {
		super(object, to, from, message, timestampInMillis);
		this.location = new URL(location);
	}

	/**
	 * Unqualified name of file sent
	 */
	public String getFilename() {
		String s = location.getFile();
		if (s.lastIndexOf("/") > 0)
			s = s.substring(s.lastIndexOf("/") + 1);
		if (s.indexOf("?") >= 0)
			s = s.substring(0, s.indexOf("?"));
		return s;
	}

	/**
	 * Accessors
	 */
	public URL getLocation() {
		return location;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString()).append(
				" location:").append(location);
		return sb.toString();
	}

}
