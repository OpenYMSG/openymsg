package org.openymsg.context.auth;

import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.execute.write.Message;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionAuthorizeImplTest {
	private SessionConfig sessionConfig;
	private Executor executor;
	private SessionAuthenticationCallback callback;
	private String username = "testuser";
	private String password = "testpassword";

	@BeforeMethod
	public void beforeMethod() {
		sessionConfig = Mockito.mock(SessionConfig.class);
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionAuthenticationCallback.class);
	}

	@Test
	public void testLogin() {
		SessionAuthenticationImpl session = new SessionAuthenticationImpl(sessionConfig, executor, callback);
		session.login(username, password);
		Mockito.verify(executor).execute(argThat(new LoginInitMessage(username)));
	}

	@Test
	public void testFailure() {
		SessionAuthenticationImpl session = new SessionAuthenticationImpl(sessionConfig, executor, callback);
		AuthenticationFailure failureState = AuthenticationFailure.BADUSERNAME;
		session.setFailureState(failureState);
		Assert.assertEquals(session.getFailureState(), failureState);
		Mockito.verify(callback).authenticationFailure(failureState);
	}

	@Test
	public void testSeed() {
		SessionAuthenticationImpl session = new SessionAuthenticationImpl(sessionConfig, executor, callback);
		String seed = "seed";
		session.login(username, password);
		AuthenticationToken token = new AuthenticationToken();
		token.setUsernameAndPassword(username, password);
		// token.setSeed(seed);
		session.receivedLoginInit();
		Mockito.verify(executor).execute(argThat(new PasswordTokenRequest(session, sessionConfig, token)));
	}

	public void test() {
		SessionAuthenticationImpl session = new SessionAuthenticationImpl(sessionConfig, executor, callback);
		String cookieY = "cookieY";
		String cookieT = "cookieT";
		String crumb = "crumb";
		String cookieB = "cookieB";
		AuthenticationToken token = new AuthenticationToken();
		token.setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
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
