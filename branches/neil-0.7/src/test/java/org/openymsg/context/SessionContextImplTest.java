package org.openymsg.context;

import org.mockito.Mockito;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionContextImplTest {
	String username = "testuser";
	Executor executor;
	SessionContextCallback callback;

	@BeforeMethod
	public void beforeMethod() {
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionContextCallback.class);
	}

	@Test
	public void test() {
		SessionConfig sessionConfig = null;
		SessionContextImpl session = new SessionContextImpl(sessionConfig, executor, username, callback);
		Assert.fail("unimplemented");
	}

}
