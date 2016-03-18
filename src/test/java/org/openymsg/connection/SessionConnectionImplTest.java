package org.openymsg.connection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.network.ConnectionEndedReason;
import org.openymsg.network.TestingConnectionBuilder;

public class SessionConnectionImplTest {
	private String username;
	private Executor executor;
	private SessionConnectionCallback listener;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public void setUp() {
		username = "testuser";
	}

	@Before
	public void setUpMethod() {
		executor = new ExecutorImpl(username);
		listener = mock(SessionConnectionCallback.class);
	}

	@After
	public void tearDownMethod() {
		executor.shutdown();
	}

	@Test()
	public void testNullConfig() {
		SessionConfig sessionConfig = null;
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		exception.expect(IllegalArgumentException.class);
		sessionConnection.initialize(sessionConfig);
	}

	@Test
	public void testConnection() {
		SessionConfig sessionConfig = mock(SessionConfig.class);
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		verify(listener, timeout(100)).connectionSuccessful();
		sessionConnection.getConnectionState();
	}

	@Test
	public void testFailed() {
		SessionConfig sessionConfig = mock(SessionConfig.class);
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(false));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		verify(listener).connectionFailure();
	}

	@Test
	public void testFailLater() {
		SessionConfig sessionConfig = mock(SessionConfig.class);
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		sessionConnection.connectionEnded(ConnectionEndedReason.SocketClosed);
		verify(listener).connectionPrematurelyEnded();
	}
}
