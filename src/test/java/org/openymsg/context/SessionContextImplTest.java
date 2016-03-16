package org.openymsg.context;

import static org.mockito.Mockito.mock;

import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionContextImplTest {
	private String username = "testuser";
	private Executor executor;
	private SessionContextCallback callback;
	private YahooConnection connection;

	@BeforeMethod
	public void beforeMethod() {
		executor = mock(Executor.class);
		callback = mock(SessionContextCallback.class);
		connection = mock(YahooConnection.class);
	}

	@Test
	public void test() {
		SessionConfig sessionConfig = null;
		SessionContextImpl session = new SessionContextImpl(sessionConfig, executor, connection, username, callback);
		Assert.fail("unimplemented");
	}

}
