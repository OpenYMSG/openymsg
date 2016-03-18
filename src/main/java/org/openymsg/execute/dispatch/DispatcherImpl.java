package org.openymsg.execute.dispatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DispatcherImpl implements Dispatcher {
	/** logger */
	private static final Log log = LogFactory.getLog(DispatcherImpl.class);
	private DispatcherExecutorService executor = null;
	private boolean shutdown;

	public DispatcherImpl(DispatcherExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public void execute(Request request) throws IllegalStateException {
		if (this.shutdown) {
			throw new IllegalStateException("Not executing because shutdown");
		} else {
			executor.execute(new RequestWrapper(request));
		}
	}

	@Override
	public void schedule(Request request, long delay) throws IllegalStateException {
		if (this.shutdown) {
			throw new IllegalStateException("Not executing because shutdown");
		} else {
			this.executor.scheduleWithFixedDelay(new RequestWrapper(request), 0, delay, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void scheduleOnce(Request request, long delay) throws IllegalStateException {
		if (this.shutdown) {
			throw new IllegalStateException("Not executing because shutdown");
		} else {
			this.executor.schedule(new RequestWrapper(request), delay, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void shutdown() {
		log.info("Shutdown dispatcher");
		this.shutdown = true;
		List<Runnable> jobs = this.executor.shutdownNow();
		for (Runnable job : jobs) {
			log.info("Shutdown with the pending job: " + job);
		}
	}

	// TODO must check
	@Override
	public boolean isTerminated() {
		return this.executor.isTerminated();
	}
}
