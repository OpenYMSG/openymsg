package org.openymsg.execute.dispatch;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatcherScheduledExecutorService extends ScheduledThreadPoolExecutor {
	private static final Log log = LogFactory.getLog(DispatcherScheduledExecutorService.class);

	public DispatcherScheduledExecutorService(String username) {
		super(1, new NamedThreadFactory(username));
		this.setKeepAliveTime(5, TimeUnit.SECONDS);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		if (t == null && r instanceof Future<?>) {
			try {
				Future<?> future = (Future<?>) r;
				if (future.isDone()) {
					future.get();
				}
			}
			catch (CancellationException ce) {
				t = ce;
			}
			catch (ExecutionException ee) {
				t = ee.getCause();
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // ignore/reset
			}
		}
		if (t != null) {
			log.error("got Exception running: " + r, t);
		}
	}

}
