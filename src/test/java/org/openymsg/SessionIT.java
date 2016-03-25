package org.openymsg;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigSimple;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.context.session.LogoutReason;

import io.github.benas.easyproperties.annotations.Property;
import io.github.benas.easyproperties.api.PropertiesInjector;
import io.github.benas.easyproperties.api.PropertyInjectionException;
// import org.testng.annotations.Parameters;
import io.github.benas.easyproperties.impl.PropertiesInjectorBuilder;

/**
 * Integration tests. Developers will need to supply the needed values for usernames and passwords. Running this often
 * will result in the users getting locked or disabled. Always use usernames you don't care about.
 * @author neilhart
 */
public class SessionIT {
	@Property(source = "users.properties", key = "badUsername")
	private String badUsername;
	@Property(source = "users.properties", key = "badUsernamePassword")
	private String badUsernamePassword;
	@Property(source = "users.properties", key = "badPasswordUsername")
	private String badPasswordUsername;
	@Property(source = "users.properties", key = "badPassword")
	private String badPassword;
	@Property(source = "users.properties", key = "username1")
	private String username1;
	@Property(source = "users.properties", key = "password1")
	private String password1;

	@Before
	public void before() throws PropertyInjectionException {
		PropertiesInjector propertiesInjector = new PropertiesInjectorBuilder().build();
		propertiesInjector.injectProperties(this);
	}

	@Test()
	@Category(SlowTest.class)
	public void testBadUsername() {
		SessionConfig config = new SessionConfigSimple();
		YahooSessionCallback callback = mock(YahooSessionCallback.class);
		YahooSession session = new SessionImpl(config, callback);
		// callback.setSession(session);
		session.login(badUsername, badUsernamePassword);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verify(callback).authenticationFailure(AuthenticationFailure.BADUSERNAME);
		assertTrue(session.isShutdown());
		assertTrue(session.isDisconnected());
	}

	@Test()
	@Category(SlowTest.class)
	public void testLocked() {
		SessionConfig config = new SessionConfigSimple();
		YahooSessionCallback callback = mock(YahooSessionCallback.class);
		YahooSession session = new SessionImpl(config, callback);
		// callback.setSession(session);
		session.login(badPasswordUsername, badPassword);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Assert.assertEquals(AuthenticationFailure.LOCKED, callback.getFailure());
		assertTrue(session.isShutdown());
		assertTrue(session.isDisconnected());
	}

	/**
	 * Start up a few sessions with no buddies and run through some tests.
	 */
	@Test()
	@Category(SlowTest.class)
	public void testTwo() {
		SessionConfig config = new SessionConfigSimple();
		YahooSessionCallback callback1a = mock(YahooSessionCallback.class);
		YahooSession session1a = new SessionImpl(config, callback1a);
		session1a.login(username1, password1);
		// test login
		try {
			Thread.sleep(4 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		verify(callback1a).authenticationSuccess();
		// test duplicate login
		YahooSessionCallback callback1 = mock(YahooSessionCallback.class);
		YahooSession session1 = new SessionImpl(config, callback1);
		session1.login(username1, password1);
		try {
			Thread.sleep(4 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		verify(callback1).authenticationSuccess();
		verify(callback1a).logoffForced(LogoutReason.DUPLICATE_LOGIN1);
		assertTrue(session1a.isShutdown());
		assertTrue(session1a.isDisconnected());
		session1.logout();
		try {
			Thread.sleep(4 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(session1.isShutdown());
		assertTrue(session1.isDisconnected());
		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setBadUsername(String badUsername) {
		this.badUsername = badUsername;
	}

	public void setBadUsernamePassword(String badUsernamePassword) {
		this.badUsernamePassword = badUsernamePassword;
	}

	public void setBadPasswordUsername(String badPasswordUsername) {
		this.badPasswordUsername = badPasswordUsername;
	}

	public void setBadPassword(String badPassword) {
		this.badPassword = badPassword;
	}

	public void setUsername1(String username1) {
		this.username1 = username1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}
}
