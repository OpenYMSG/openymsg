package org.openymsg.execute.dispatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatcherExecutorLoggingCallback implements DispatcherExecutorCallback {
	private static final Log log = LogFactory.getLog(DispatcherExecutorLoggingCallback.class);

	@Override
	public void afterExecute(Runnable runnable, Throwable throwable) {
		log.error("got Exception running: " + runnable, throwable);
	}

	@Override
	public void rejectedExecution(Runnable runnable) {
		log.error("got rejected running: " + runnable);
	}

}
