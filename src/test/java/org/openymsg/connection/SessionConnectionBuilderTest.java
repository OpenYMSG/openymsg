package org.openymsg.connection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;

public class SessionConnectionBuilderTest {
	private SessionConnectionBuilder builder = new SessionConnectionBuilder();
	@Mock
	private SessionConnectionCallback callback;
	@Mock
	private Executor executor;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test()
	public void testNullConfig() {
		SessionConfig sessionConfig = null;
		exception.expect(IllegalStateException.class);
		exception.expectMessage("sessionConfig must not be null");
		builder.setCallback(callback).setExecutor(executor).setSessionConfig(sessionConfig).build();
	}
}
