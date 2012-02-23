package org.openymsg.execute.dispatch;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Request;

public class SimpleExecutor { //implements Dispatcher {
	private static final Log log = LogFactory.getLog(SimpleExecutor.class);
	private ScheduledThreadPoolExecutor executor = null;
	private boolean shutdown;

	public SimpleExecutor(ScheduledThreadPoolExecutor executor) {
		this.executor = executor;
	}

	public void execute(Request request) {
		if (this.shutdown) {
			log.warn("Not executing: " + request + ", " + this.executor.isShutdown());
		}
		else {
			executor.execute(new RequestWrapper(request));
		}
	}
	
	public void schedule(Runnable runnable, long delay) {
		this.executor.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MILLISECONDS);
	}
	
//	@Override
	public void shutdown() {
		log.info("Shutdown dispatcher");
		this.shutdown = true;
		this.executor.shutdown();
	}
}
