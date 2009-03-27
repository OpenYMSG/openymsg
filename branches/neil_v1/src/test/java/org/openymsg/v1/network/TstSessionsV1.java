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
package org.openymsg.v1.network;

import org.openymsg.network.Session;
import org.openymsg.network.TstSessions;
import org.openymsg.network.event.WaitListener;
import org.openymsg.v1.roster.RosterV1;

public class TstSessionsV1 extends TstSessions<RosterV1, YahooUserV1> {

	public TstSessionsV1() throws Throwable {
		super();
	}

	@Override
	protected Session<RosterV1, YahooUserV1> createSession() {
		return new SessionV1();
	}
	
	@Override
	public Session<RosterV1, YahooUserV1> getSess1() {
		return sess1;
	}

	@Override
	public Session<RosterV1, YahooUserV1> getSess2() {
		return sess2;
	}

	@Override
	public WaitListener getListener1() {
		return listener1;
	}

	@Override
	public WaitListener getListener2() {
		return listener2;
	}


}
