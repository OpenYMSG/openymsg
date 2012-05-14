package org.openymsg.execute;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.execute.dispatch.DispatcherExecutorService;
import org.openymsg.execute.dispatch.DispatcherImpl;
import org.openymsg.execute.dispatch.Request;

public class ExecutorImpl implements Executor {
	// private ScheduledThreadPoolExecutor executor = null;
	private Dispatcher dispatcher;

	public ExecutorImpl(String username) {
		DispatcherExecutorService executor = new DispatcherExecutorService(username);
		this.dispatcher = new DispatcherImpl(executor);
	}

	@Override
	public void execute(Request request) throws IllegalStateException {
		this.dispatcher.execute(request);
	}

	@Override
	public void shutdown() {
		this.dispatcher.shutdown();
	}

	@Override
	public void schedule(Request request, long repeatInterval) {
		this.dispatcher.schedule(request, repeatInterval);
	}

	@Override
	public boolean isTerminated() {
		return this.dispatcher.isTerminated();
	}

}
