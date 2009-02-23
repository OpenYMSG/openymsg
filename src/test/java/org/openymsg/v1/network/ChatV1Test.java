package org.openymsg.v1.network;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openymsg.network.ChatTest;
import org.openymsg.v1.roster.RosterV1;

public class ChatV1Test extends ChatTest<RosterV1, YahooUserV1> {
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
