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
	public void testCancelScheduled() {
		String name = "name";
		TestingDispatcherExecutorCallback callback = new TestingDispatcherExecutorCallback();
		DispatcherExecutorService executor = new DispatcherExecutorService(name, callback);
		QuiteRunnable runnable = new QuiteRunnable();
		executor.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
		System.out.println(executor.getCompletedTaskCount() + "/" + executor.getQueue().size());
		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(executor.getCompletedTaskCount() + "/" + executor.getQueue().size());
		Assert.assertTrue(runnable.checkAndResetHasRun());
		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(runnable.checkAndResetHasRun());
		System.out.println(executor.getCompletedTaskCount() + "/" + executor.getQueue().size());
		runnable.setException(new ScheduleTaskCompletionException());
		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(executor.getCompletedTaskCount() + "/" + executor.getQueue().size());
		Assert.assertFalse(runnable.hasRun());
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
		Assert.assertFalse(((QuiteRunnable) runnable).hasRun());
		Assert.assertNotNull(callback.getRunnable(), "Should have an unrun runnable");
	}

	private final class QuiteRunnable implements Runnable {
		boolean ran = false;
		RuntimeException exception = null;

		@Override
		public void run() {
			if (exception != null) {
				throw exception;
			}
			this.ran = true;
		}

		public boolean hasRun() {
			return this.ran;
		}

		public boolean checkAndResetHasRun() {
			boolean answer = this.ran;
			this.ran = false;
			return answer;
		}

		/**
		 * @param exception the exception to set
		 */
		public void setException(RuntimeException exception) {
			this.exception = exception;
		}
	}

	private class SlowRunnable implements Runnable {
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
