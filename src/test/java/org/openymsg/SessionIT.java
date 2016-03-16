package org.openymsg;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.Assert;

import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigSimple;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.context.session.LogoutReason;
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
		SessionConfig config = new SessionConfigSimple();
		YahooSessionCallback callback = mock(YahooSessionCallback.class);
		YahooSession session = new SessionImpl(config, callback);
		// callback.setSession(session);
		session.login(badUsername, badUsernamePassword);

		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verify(callback).authenticationFailure(AuthenticationFailure.BADUSERNAME);
		Assert.assertTrue(session.isShutdown());
		Assert.assertTrue(session.isDisconnected());

	}

	@Parameters({ "badPasswordUsername", "badPassword" })
	@Test()
	public void testLocked(String badPasswordUsername, String badPassword) {
		SessionConfig config = new SessionConfigSimple();
		YahooSessionCallback callback = mock(YahooSessionCallback.class);
		YahooSession session = new SessionImpl(config, callback);
		// callback.setSession(session);
		session.login(badPasswordUsername, badPassword);

		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Assert.assertEquals(AuthenticationFailure.LOCKED, callback.getFailure());
		Assert.assertTrue(session.isShutdown());
		Assert.assertTrue(session.isDisconnected());

	}

	/**
	 * Start up a few sessions with no buddies and run through some tests.
	 * @param username1
	 * @param password1, String username2, String password2, String username3, String password3
	 */
	@Parameters({ "username2", "password2", "username3", "password3", "username1", "password1" })
	@Test()
	public void testTwo(String username1, String password1, String username2, String password2, String username3,
			String password3) {
		SessionConfig config = new SessionConfigSimple();
		YahooSessionCallback callback1a = mock(YahooSessionCallback.class);
		YahooSession session1a = new SessionImpl(config, callback1a);
		session1a.login(username1, password1);

		// test login
		try {
			Thread.sleep(4 * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		verify(callback1a).authenticationSuccess();

		// test duplicate login
		YahooSessionCallback callback1 = mock(YahooSessionCallback.class);
		YahooSession session1 = new SessionImpl(config, callback1);
		session1.login(username1, password1);

		try {
			Thread.sleep(4 * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		verify(callback1).authenticationSuccess();
		verify(callback1a).logoffForced(LogoutReason.DUPLICATE_LOGIN1);
		Assert.assertTrue(session1a.isShutdown());
		Assert.assertTrue(session1a.isDisconnected());

		session1.logout();

		try {
			Thread.sleep(4 * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertTrue(session1.isShutdown());
		Assert.assertTrue(session1.isDisconnected());

		try {
			Thread.sleep(2 * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
