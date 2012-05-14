package org.openymsg.context;

import org.mockito.Mockito;
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
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionContextCallback.class);
		connection = Mockito.mock(YahooConnection.class);
	}

	@Test
	public void test() {
		SessionConfig sessionConfig = null;
		SessionContextImpl session = new SessionContextImpl(sessionConfig, executor, connection, username, callback);
		Assert.fail("unimplemented");
	}

}
