package org.openymsg.connection;

import org.mockito.Mockito;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.network.TestingConnectionBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionConnectionImplTest {
	private String username;
	private Executor executor;
	private SessionConnectionCallback listener;

	@BeforeClass
	public void setUp() {
		username = "testuser";
	}

	@BeforeMethod
	public void setUpMethod() {
		executor = new ExecutorImpl(username);
		listener = Mockito.mock(SessionConnectionCallback.class);
	}

	@AfterMethod
	public void tearDownMethod() {
		executor.shutdown();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testNullConfig() {
		SessionConfig sessionConfig = null;
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
	}

	@Test
	public void testConnection() {
		SessionConfig sessionConfig = Mockito.mock(SessionConfig.class);
		Mockito.when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		Mockito.verify(listener, Mockito.timeout(100)).connectionSuccessful();
		sessionConnection.getConnectionState();
	}

	@Test
	public void testFailed() {
		SessionConfig sessionConfig = Mockito.mock(SessionConfig.class);
		Mockito.when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(false));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		Mockito.verify(listener).connectionFailure();
	}

	@Test
	public void testFailLater() {
		SessionConfig sessionConfig = Mockito.mock(SessionConfig.class);
		Mockito.when(sessionConfig.getConnectionBuilder()).thenReturn(new TestingConnectionBuilder(true));
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, listener);
		sessionConnection.initialize(sessionConfig);
		sessionConnection.connectionEnded();
		Mockito.verify(listener).connectionPrematurelyEnded();
	}

}
