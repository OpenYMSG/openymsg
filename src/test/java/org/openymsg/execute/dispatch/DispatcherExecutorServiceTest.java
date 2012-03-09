package org.openymsg.execute.dispatch;

import org.testng.annotations.Test;

public class DispatcherExecutorServiceTest {

	@Test
	public void testException() {
		String name = "name";
		DispatcherExecutorService executor = new DispatcherExecutorService(name);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				throw new RuntimeException("test failure");
			}

		};
		executor.execute(runnable);
	}
}
