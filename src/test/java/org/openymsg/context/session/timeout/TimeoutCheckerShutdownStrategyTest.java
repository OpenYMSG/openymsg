package org.openymsg.context.session.timeout;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.openymsg.connection.YahooConnection;
import org.openymsg.context.session.ShutdownRequest;
import org.openymsg.execute.Executor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TimeoutCheckerShutdownStrategyTest {
	private Executor executor;
	private YahooConnection connection;

	@BeforeMethod
	public void beforeMethod() {
		executor = mock(Executor.class);
		connection = mock(YahooConnection.class);
	}

	public void testShutdown() throws InterruptedException {
		int timeout = 1;
		TimeoutCheckerShutdownStrategy strategy = new TimeoutCheckerShutdownStrategy(timeout, executor, connection);
		strategy.execute();
		Thread.sleep(2 * 1000);
		strategy.execute();
		verify(executor).execute(new ShutdownRequest(connection));
	}

	public void testKeepAlive() throws InterruptedException {
		int timeout = 2;
		TimeoutCheckerShutdownStrategy strategy = new TimeoutCheckerShutdownStrategy(timeout, executor, connection);
		strategy.keepAlive();
		strategy.execute();
		Thread.sleep(1 * 1000);
		strategy.execute();
		strategy.keepAlive();
		strategy.execute();
		Thread.sleep(1 * 1000);
	}

}
