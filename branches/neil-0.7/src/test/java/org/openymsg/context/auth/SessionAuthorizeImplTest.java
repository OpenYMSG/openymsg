package org.openymsg.context.auth;

import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.write.Message;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.Request;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionAuthorizeImplTest {
	private SessionConfig sessionConfig;
	private Executor executor;
	private YahooConnection connection;
	private SessionAuthenticationCallback callback;
	private SessionAuthenticationImpl session;
	private String username = "testuser";
	private String password = "testpassword";

	@BeforeMethod
	public void beforeMethod() {
		sessionConfig = Mockito.mock(SessionConfig.class);
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionAuthenticationCallback.class);
		connection = Mockito.mock(YahooConnection.class);
		session = new SessionAuthenticationImpl(sessionConfig, connection, executor, callback);
	}

	@Test
	public void testLogin() {
		session.login(username, password);
		Mockito.verify(connection).execute(argThat(new LoginInitMessage(username)));
	}

	@Test
	public void testFailure() {
		AuthenticationFailure failureState = AuthenticationFailure.BADUSERNAME;
		session.setFailureState(failureState);
		Assert.assertEquals(session.getFailureState(), failureState);
		Mockito.verify(callback).authenticationFailure(failureState);
	}

	@Test
	public void testSeed() {
		session.login(username, password);
		AuthenticationToken token = new AuthenticationToken();
		token.setUsernameAndPassword(username, password);
		session.receivedLoginInit();
		Mockito.verify(executor).execute(argThat(new PasswordTokenRequest(session, sessionConfig, token)));
	}

	public void test() {
		String cookieY = "cookieY";
		String cookieT = "cookieT";
		String crumb = "crumb";
		String cookieB = "cookieB";
		session.receivedPasswordTokenLogin();
		AuthenticationToken token = new AuthenticationToken();
		token.setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
		Mockito.verify(executor).execute(argThat(new PasswordTokenLoginRequest(session, sessionConfig, token)));
	}

	// TODO copied
	private Message argThat(Message message, String... excludeFields) {
		return (Message) Mockito.argThat(new ReflectionEquals(message, excludeFields));
	}

	// TODO copied
	private Request argThat(Request request, String... excludeFields) {
		return (Request) Mockito.argThat(new ReflectionEquals(request, excludeFields));
	}

}
