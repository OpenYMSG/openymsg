package org.openymsg.execute.dispatch;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatcherImpl implements Dispatcher {
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
	public void shutdown() {
		log.info("Shutdown dispatcher");
		this.shutdown = true;
		this.executor.shutdown();
	}
}
