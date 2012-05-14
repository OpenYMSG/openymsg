package org.openymsg;

import junit.framework.Assert;

import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigImpl;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.testing.TestingSessionCallback;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Integration tests. Developers will need to supply the needed values for usernames and passwords. Running this often
 * will result in the users getting locked or disabled. Always use usernames you don't care about.
 * @author neilhart
 */
public class SessionIT {

	@Parameters({ "badUsername", "badUsernamePassword" })
	@Test()
	public void testBadUsername(String badUsername, String badUsernamePassword) {
		SessionConfig config = new SessionConfigImpl();
		TestingSessionCallback callback = new TestingSessionCallback();
		YahooSession session = new SessionImpl(config, callback);
		// callback.setSession(session);
		session.login(badUsername, badUsernamePassword);

		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(AuthenticationFailure.BADUSERNAME, callback.getFailure());
		Assert.assertTrue(session.isShutdown());
		Assert.assertTrue(session.isDisconnected());

	}

	@Parameters({ "badPasswordUsername", "badPassword" })
	@Test()
	public void testLocked(String badPasswordUsername, String badPassword) {
		SessionConfig config = new SessionConfigImpl();
		TestingSessionCallback callback = new TestingSessionCallback();
		YahooSession session = new SessionImpl(config, callback);
		// callback.setSession(session);
		session.login(badPasswordUsername, badPassword);

		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(AuthenticationFailure.LOCKED, callback.getFailure());
		Assert.assertTrue(session.isShutdown());
		Assert.assertTrue(session.isDisconnected());

	}

	/**
	 * Start up a few sessions with no buddies and run through some tests.
	 * @param username1
	 * @param password1, String username2, String password2, String username3, String password3
	 */
	@Parameters({ "username1", "password1", "username2", "password2", "username3", "password3" })
	@Test()
	public void testTwo(String username1, String password1, String username2, String password2, String username3,
			String password3) {
		SessionConfig config = new SessionConfigImpl();
		YahooSessionCallback callback1 = new TestingSessionCallback();
		YahooSession session1 = new SessionImpl(config, callback1);
		YahooSessionCallback callback2 = new TestingSessionCallback();
		YahooSession session2 = new SessionImpl(config, callback2);
		YahooSessionCallback callback3 = new TestingSessionCallback();
		YahooSession session3 = new SessionImpl(config, callback3);
		session1.login(username1, password1);
		session2.login(username2, password2);
		session3.login(username3, password3);

		try {
			Thread.sleep(5 * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(10 * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
