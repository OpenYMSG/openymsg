package org.openymsg.execute.dispatch;

public interface DispatcherExecutorCallback {
	void afterExecute(Runnable r, Throwable t);

	void rejectedExecution(Runnable runnable);
}
