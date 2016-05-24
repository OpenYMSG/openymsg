package org.openymsg.context.auth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openymsg.testing.MessageAssert.argThatMessage;
import static org.openymsg.testing.MessageAssert.argThatRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.read.ReaderRegistry;
import org.openymsg.connection.write.PacketWriter;
import org.openymsg.execute.Executor;

public class SessionAuthorizeImplTest {
	@Mock
	private SessionConfig sessionConfig;
	@Mock
	private Executor executor;
	@Mock
	private YahooConnection connection;
	@Mock
	private SessionAuthenticationCallback callback;
	@Mock
	private PacketWriter writer;
	@Mock
	private ReaderRegistry registry;
	private SessionAuthenticationImpl session;
	private String username = "testuser";
	private String password = "testpassword";

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		when(connection.getPacketWriter()).thenReturn(writer);
		when(connection.getReaderRegistry()).thenReturn(registry);
		session = new SessionAuthenticationImpl(sessionConfig, connection, executor, callback);
	}

	@Test
	public void testLogin() {
		session.login(username, password, false);
		verify(writer).execute(argThatMessage(new LoginInitMessage(username)));
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
		session.login(username, password, false);
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
