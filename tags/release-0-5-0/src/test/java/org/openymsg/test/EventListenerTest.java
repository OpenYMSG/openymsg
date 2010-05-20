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
package org.openymsg.test;

import org.junit.Test;
import org.openymsg.network.Session;
import org.openymsg.network.event.SessionAdapter;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class EventListenerTest {
    @Test
    public void testAddEventListenerBeforeLoggingIn() throws Exception {
        final Session session = new Session();
        session.addSessionListener(new SessionAdapter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNull() throws Exception {
        final Session session = new Session();
        session.addSessionListener(null);
    }
}
