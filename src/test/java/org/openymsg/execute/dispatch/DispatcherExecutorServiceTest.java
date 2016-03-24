package org.openymsg.execute.dispatch;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DispatcherExecutorServiceTest {
	@Test
	public void testException() {
		String name = "name";
		TestingDispatcherExecutorCallback callback = new TestingDispatcherExecutorCallback();
		DispatcherExecutorService executor = new DispatcherExecutorService(name, callback);
		Runnable runnable = new ExceptionRunnable();
		executor.execute(runnable);
		Throwable t = callback.getException();
		assertNotNull("Should have an exception", t);
	}

	@Test
	@Category(org.openymsg.SlowTest.class)
	public void testCancelScheduled() {
		String name = "name";
		TestingDispatcherExecutorCallback callback = new TestingDispatcherExecutorCallback();
		DispatcherExecutorService executor = new DispatcherExecutorService(name, callback);
		QuiteRunnable runnable = new QuiteRunnable();
		executor.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
		await().atMost(1100, TimeUnit.MILLISECONDS).until(runnable.getRunCount(), equalTo(1));
		await().atMost(1100, TimeUnit.MILLISECONDS).until(runnable.getRunCount(), equalTo(2));
		await().atMost(1100, TimeUnit.MILLISECONDS).until(runnable.getRunCount(), equalTo(3));
		runnable.setException(new ScheduleTaskCompletionException());
		await().atMost(1100, TimeUnit.MILLISECONDS).until(runnable.hasThrowException());
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
		assertNull("Should not have an exception", callback.getException());
		executor.shutdownNow();
		runnable = new QuiteRunnable();
		executor.execute(runnable);
		assertFalse(((QuiteRunnable) runnable).hasRun());
		assertNotNull("Should have an unrun runnable", callback.getRunnable());
	}

	private final class QuiteRunnable implements Runnable {
		int runCount = 0;
		RuntimeException exception = null;
		boolean threwException = false;

		@Override
		public void run() {
			if (exception != null) {
				threwException = true;
				throw exception;
			}
			this.runCount++;
		}

		private Callable<Integer> getRunCount() {
			return new Callable<Integer>() {
				public Integer call() throws Exception {
					return runCount;
				}
			};
		}

		private Callable<Boolean> hasThrowException() {
			return new Callable<Boolean>() {
				public Boolean call() throws Exception {
					return threwException;
				}
			};
		}

		public boolean hasRun() {
			return this.runCount > 1;
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
			} catch (InterruptedException e) {
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		Runnable getRunnable() {
			try {
				return this.runnables.poll(100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
