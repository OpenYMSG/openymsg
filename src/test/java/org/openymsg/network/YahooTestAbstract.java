/**
 * 
 */
package org.openymsg.network;

import org.openymsg.network.event.WaitListener;
import org.openymsg.roster.Roster;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 * 
 */
public abstract class YahooTestAbstract<T extends Roster<U>, U extends YahooUser> {

	protected static final String CHATMESSAGE = "CHATMESSAGE";
	protected Session<T, U> sess1;
	protected Session<T, U> sess2;
	protected WaitListener listener1;
	protected WaitListener listener2;
	protected TstSessions<T, U> testSession;

	protected abstract U createUser(String userId, String group);

//	protected static TestSessions<Roster<YahooUser>, YahooUser> TEST_SESSIONS;
//	/**
//	 * @throws Throwable
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Throwable {
//		TEST_SESSIONS = new TestSessions();
//	}
//	
//	@Before
//	public void setUp()  throws Throwable{
//		sess1 = TEST_SESSIONS.getSess1();
//		sess2 = TEST_SESSIONS.getSess2();
//		listener1 = TEST_SESSIONS.getListener1();
//		listener2 = TEST_SESSIONS.getListener2();
//	}
//
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		TEST_SESSIONS.dispose();
//	}


}
