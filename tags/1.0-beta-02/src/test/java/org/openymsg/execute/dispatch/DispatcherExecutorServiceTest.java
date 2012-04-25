package org.openymsg.execute.dispatch;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DispatcherExecutorServiceTest {

	@Test
	public void testException() {
		String name = "name";
		TestingDispatcherExecutorCallback callback = new TestingDispatcherExecutorCallback();
		DispatcherExecutorService executor = new DispatcherExecutorService(name, callback);
		Runnable runnable = new ExceptionRunnable();
		executor.execute(runnable);
		Throwable t = callback.getException();
		Assert.assertNotNull(t, "Should have an exception");
	}

	@Test
	public void testShutdown() {
		String name = "name";
		TestingDispatcherExecutorCallback callback = new TestingDispatcherExecutorCallback();
		DispatcherExecutorService executor = new DispatcherExecutorService(name, callback);
		Runnable runnable = new QuiteRunnable();
		executor.execute(runnable);
		executor.execute(new SlowRunnable());
		executor.execute(new SlowRunnable());
		Assert.assertNull(callback.getException(), "Should not have an exception");
		executor.shutdownNow();
		runnable = new QuiteRunnable();
		executor.execute(runnable);
		Assert.assertNotNull(callback.getRunnable(), "Should have an unrun runnable");
	}

	private final class QuiteRunnable implements Runnable {
		boolean ran = false;

		@Override
		public void run() {
			this.ran = true;
		}

		public boolean hasRun() {
			return this.ran;
		}
	}

	private final class SlowRunnable implements Runnable {
		boolean ran = false;

		@Override
		public void run() {
			this.ran = true;
			try {
				Thread.sleep(3000);
			}
			catch (InterruptedException e) {
				// e.printStackTrace();
			}
		}

		public boolean hasRun() {
			return this.ran;
		}
	}

	private final class ExceptionRunnable implements Runnable {
		@Override
		public void run() {
			throw new RuntimeException("test failure");
		}
	}

	private final class TestingDispatcherExecutorCallback implements DispatcherExecutorCallback {
		private LinkedBlockingQueue<Throwable> exceptions = new LinkedBlockingQueue<Throwable>();
		private LinkedBlockingQueue<Runnable> runnables = new LinkedBlockingQueue<Runnable>();

		@Override
		public void rejectedExecution(Runnable runnable) {
			this.runnables.add(runnable);
		}

		@Override
		public void afterExecute(Runnable r, Throwable t) {
			this.exceptions.add(t);
		}

		Throwable getException() {
			try {
				return this.exceptions.poll(100, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		Runnable getRunnable() {
			try {
				return this.runnables.poll(100, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
