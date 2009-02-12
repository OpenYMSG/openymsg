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

import static junit.framework.Assert.assertEquals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.openymsg.network.Session;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class SessionTest {
	@Test
	public void testSession() throws Exception {
		new Session();
	}

	@Test
	public void testConcurrentSessions() throws Exception {
		Set<Session> sessions = new HashSet<Session>();
		final int max = 10;
		for (int i = 0; i < max; i++) {
			sessions.add(new Session());
		}

		assertEquals(max, sessions.size());

		Iterator<Session> it = sessions.iterator();
		int i = 0;
		while (it.hasNext()) {
			i++;
			it.next();
			it.remove();
		}

		assertEquals(max, i);
		assertEquals(0, sessions.size());
	}
}
