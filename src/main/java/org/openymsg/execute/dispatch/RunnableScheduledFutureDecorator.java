package org.openymsg.execute.dispatch;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RunnableScheduledFutureDecorator<V> implements RunnableScheduledFuture<V> {
	private final RunnableScheduledFuture<V> future;
	private final Runnable runnable;
	private final Callable<V> callable;

	public RunnableScheduledFutureDecorator(Runnable runnable, RunnableScheduledFuture<V> future) {
		this.runnable = runnable;
		this.future = future;
		this.callable = null;
	}

	public RunnableScheduledFutureDecorator(Callable<V> callable, RunnableScheduledFuture<V> future) {
		this.runnable = null;
		this.future = future;
		this.callable = callable;
	}

	@Override
	public void run() {
		future.run();
	}

	@Override
	public boolean cancel(boolean shouldCancel) {
		return future.cancel(shouldCancel);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public V get(long paramLong, TimeUnit paramTimeUnit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(paramLong, paramTimeUnit);
	}

	@Override
	public long getDelay(TimeUnit paramTimeUnit) {
		return future.getDelay(paramTimeUnit);
	}

	@Override
	public int compareTo(Delayed paramT) {
		return future.compareTo(paramT);
	}

	@Override
	public boolean isPeriodic() {
		return future.isPeriodic();
	}

	@Override
	public String toString() {
		if (runnable == null) {
			return String.format("RunnableScheduledFutureDecorator [callable=%s]", callable);
		} else {
			return String.format("RunnableScheduledFutureDecorator [runnable=%s]", runnable);
		}
	}

}
