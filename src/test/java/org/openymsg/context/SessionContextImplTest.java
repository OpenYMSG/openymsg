package org.openymsg.context;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;

public class SessionContextImplTest {
	private String username = "testuser";
	private Executor executor;
	private SessionContextCallback callback;
	private YahooConnection connection;

	@Before
	public void beforeMethod() {
		executor = mock(Executor.class);
		callback = mock(SessionContextCallback.class);
		connection = mock(YahooConnection.class);
	}

	@Test
	public void test() {
		SessionConfig sessionConfig = null;
		SessionContextImpl session = new SessionContextImpl(sessionConfig, executor, connection, username, callback);
		fail("unimplemented");
	}
}
