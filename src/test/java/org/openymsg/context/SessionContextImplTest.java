package org.openymsg.context;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;

public class SessionContextImplTest {
	private String username = "testuser";
	@Mock
	private Executor executor;
	@Mock
	private SessionContextCallback callback;
	@Mock
	private YahooConnection connection;
	@Mock
	private SessionConfig sessionConfig;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		SessionContextImpl session = new SessionContextImpl(sessionConfig, executor, connection, username, callback);
		fail("unimplemented");
	}
}
