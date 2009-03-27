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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openymsg.network.StatusTest;
import org.openymsg.v1.roster.RosterV1;

public class StatusV1Test extends StatusTest<RosterV1, YahooUserV1> {
	protected static TstSessionsV1 TEST_SESSIONS;
	/**
	 * @throws Throwable
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Throwable {
		TEST_SESSIONS = new TstSessionsV1();
	}
	
	@Before
	public void setUp()  throws Throwable{
		testSession = TEST_SESSIONS;
		sess1 = TEST_SESSIONS.getSess1();
		sess2 = TEST_SESSIONS.getSess2();
		listener1 = TEST_SESSIONS.getListener1();
		listener2 = TEST_SESSIONS.getListener2();
	}


	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TEST_SESSIONS.dispose();
	}

	@Override
	protected YahooUserV1 createUser(String userId, String group) {
		return new YahooUserV1(userId, group);
	}


}
