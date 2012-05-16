package org.openymsg.execute.dispatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatcherExecutorLoggingCallback implements DispatcherExecutorCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(DispatcherExecutorLoggingCallback.class);

	@Override
	public void afterExecute(Runnable runnable, Throwable throwable) {
		if (throwable instanceof ScheduleTaskCompletionException) {
			// TODO get runnable name. this is a ScheduleFutureTask
			log.debug("scheduled runnable is done: " + runnable);
		} else {
			log.error("got Exception running: " + runnable, throwable);
		}
	}

	@Override
	public void rejectedExecution(Runnable runnable) {
		log.error("got rejected running: " + runnable);
	}

}
