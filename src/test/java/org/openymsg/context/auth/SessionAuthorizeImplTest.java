package org.openymsg.context.auth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openymsg.testing.MessageAssert.argThatMessage;
import static org.openymsg.testing.MessageAssert.argThatRequest;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;

public class SessionAuthorizeImplTest {
	private SessionConfig sessionConfig;
	private Executor executor;
	private YahooConnection connection;
	private SessionAuthenticationCallback callback;
	private SessionAuthenticationImpl session;
	private String username = "testuser";
	private String password = "testpassword";

	@Before
	public void beforeMethod() {
		sessionConfig = mock(SessionConfig.class);
		executor = mock(Executor.class);
		callback = mock(SessionAuthenticationCallback.class);
		connection = mock(YahooConnection.class);
		session = new SessionAuthenticationImpl(sessionConfig, connection, executor, callback);
	}

	@Test
	public void testLogin() {
		session.login(username, password);
		verify(connection).execute(argThatMessage(new LoginInitMessage(username)));
	}

	@Test
	public void testFailure() {
		AuthenticationFailure failureState = AuthenticationFailure.BADUSERNAME;
		session.setFailureState(failureState);
		assertEquals(session.getFailureState(), failureState);
		verify(callback).authenticationFailure(failureState);
	}

	@Test
	public void testSeed() {
		session.login(username, password);
		AuthenticationToken token = new AuthenticationToken();
		token.setUsernameAndPassword(username, password);
		session.receivedLoginInit();
		verify(executor).execute(argThatRequest(new PasswordTokenRequest(session, sessionConfig, token)));
	}

	public void test() {
		String cookieY = "cookieY";
		String cookieT = "cookieT";
		String crumb = "crumb";
		String cookieB = "cookieB";
		session.receivedPasswordTokenLogin();
		AuthenticationToken token = new AuthenticationToken();
		token.setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
		verify(executor).execute(argThatRequest(new PasswordTokenLoginRequest(session, sessionConfig, token)));
	}
}
