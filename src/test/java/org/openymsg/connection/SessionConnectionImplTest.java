package org.openymsg.connection;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.network.ConnectionEndedReason;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.TestingConnectionBuilder;

public class SessionConnectionImplTest {
	private static String username;
	private Executor executor;
	@Mock
	private SessionConnectionCallback listener;
	@Mock
	SessionConfig sessionConfig;
	@Mock
	private ConnectionHandler connection;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUp() {
		username = "testuser";
	}

	@Before
	public void setUpMethod() {
		MockitoAnnotations.initMocks(this);
		executor = new ExecutorImpl(username);
	}

	@After
	public void tearDownMethod() {
		executor.shutdown();
	}

	@Test()
	public void testNullConfig() {
		sessionConfig = null;
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		exception.expect(IllegalArgumentException.class);
		sessionConnection.initialize(sessionConfig);
	}

	@Test
	public void testConnection() {
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		verify(listener, timeout(100)).connectionSuccessful();
		sessionConnection.getConnectionState();
	}

	@Test
	@Ignore
	/** Test that a connection never connects */
	public void testFailed() {
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(false));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		verify(listener, timeout(100)).connectionFailure();
	}

	@Test
	public void testFailLater() {
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		sessionConnection.initializeConnection(connection);
		sessionConnection.connectionEnded(ConnectionEndedReason.SocketClosed);
		verify(listener).connectionPrematurelyEnded();
	}
}
