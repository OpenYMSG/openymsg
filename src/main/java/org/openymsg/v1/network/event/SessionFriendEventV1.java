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
package org.openymsg.v1.network.event;

import org.openymsg.network.event.AbstractSessionFriendEvent;
import org.openymsg.v1.network.YahooUserV1;


/**
 * Represents an event triggered by a change in the presence or roster change of
 * a session. This Event typically gets thrown after a friend has been added,
 * removed or updated.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionFriendEventV1 extends AbstractSessionFriendEvent<YahooUserV1> {
	private static final long serialVersionUID = -2887428479487414186L;

	public SessionFriendEventV1(Object source, YahooUserV1 user) {
		super(source, user);
	}

	protected SessionFriendEventV1(Object source, String message, YahooUserV1 user) {
		super(source, message, user);
	}
}
