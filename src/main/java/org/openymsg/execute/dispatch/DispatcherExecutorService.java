package org.openymsg.execute.dispatch;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatcherExecutorService extends ScheduledThreadPoolExecutor {
	DispatcherExecutorCallback callback;

	public DispatcherExecutorService(String name, DispatcherExecutorCallback callback) {
		super(1, new NamedThreadFactory(name), new RejectionWrapper(callback));
		this.callback = callback;
		this.setKeepAliveTime(5, TimeUnit.SECONDS);
		this.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		this.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
	}

	public DispatcherExecutorService(String name) {
		this(name, new DispatcherExecutorLoggingCallback());
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		if (t == null && r instanceof Future<?>) {
			try {
				Future<?> future = (Future<?>) r;
				if (future.isDone()) {
					future.get();
				}
			} catch (CancellationException ce) {
				t = ce;
			} catch (ExecutionException ee) {
				t = ee.getCause();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // ignore/reset
			}
		}
		if (t != null) {
			this.callback.afterExecute(r, t);
		}
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
			RunnableScheduledFuture<V> scheduledFuture) {
		return new RunnableScheduledFutureDecorator<V>(runnable, scheduledFuture);
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable,
			RunnableScheduledFuture<V> scheduledFuture) {
		return new RunnableScheduledFutureDecorator<V>(callable, scheduledFuture);
	}
}
