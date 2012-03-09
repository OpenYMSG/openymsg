package org.openymsg.execute.dispatch;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectionWrapper implements RejectedExecutionHandler {

	private DispatcherExecutorCallback callback;

	public RejectionWrapper(DispatcherExecutorCallback callback) {
		this.callback = callback;
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor arg1) {
		this.callback.rejectedExecution(runnable);

	}

}
