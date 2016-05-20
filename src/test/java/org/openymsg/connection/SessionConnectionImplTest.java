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
	private SessionConnectionBuilder builder = new SessionConnectionBuilder();
	private static String username;
	private Executor executor;
	@Mock
	private SessionConnectionCallback callback;
	@Mock
	SessionConfig sessionConfig;
	@Mock
	private ConnectionHandler connection;
	@Mock
	private ConnectionInfo status;
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

	@Test
	public void testConnection() {
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection =
				builder.setCallback(callback).setExecutor(executor).setSessionConfig(sessionConfig).build();
		verify(callback, timeout(100)).connectionSuccessful();
		sessionConnection.getConnectionState();
	}

	@Test
	@Ignore
	/** Test that a connection never connects */
	public void testFailed() {
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(false));
		builder.setCallback(callback).setExecutor(executor).setSessionConfig(sessionConfig).build();
		verify(callback, timeout(100)).connectionFailure();
	}

	@Test
	public void testFailLater() {
		when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		builder.setCallback(callback).setExecutor(executor).setSessionConfig(sessionConfig).build();
		ConnectionStateAndDetails state = builder.getConnectionStateForTest();
		state.setConnected(connection, status);
		state.connectionEnded(ConnectionEndedReason.SocketClosed);
		verify(callback).connectionPrematurelyEnded();
	}
}
