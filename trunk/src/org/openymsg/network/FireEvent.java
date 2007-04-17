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

import org.openymsg.network.event.SessionEvent;

/**
 * Wrapper for combining a SessionEvent with a type. This makes the event
 * dispatchable to eventlisteners.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
// TODO: see if this class can be removed by adding 'type' as a member of
// SessionEvent
public class FireEvent {
	private volatile ServiceType type;

	private volatile SessionEvent event;

	public FireEvent(SessionEvent event, ServiceType type) {
		this.event = event;
		this.type = type;
	}

	/**
	 * @return the event
	 */
	public SessionEvent getEvent() {
		return event;
	}

	/**
	 * @return the type
	 */
	public ServiceType getType() {
		return type;
	}
}
