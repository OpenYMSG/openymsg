package org.openymsg.execute;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.execute.dispatch.DispatcherExecutorService;
import org.openymsg.execute.dispatch.DispatcherImpl;
import org.openymsg.execute.dispatch.Request;

public class ExecutorImpl implements Executor {
	private Dispatcher dispatcher;

	public ExecutorImpl(String username) {
		DispatcherExecutorService executor = new DispatcherExecutorService(username);
		dispatcher = new DispatcherImpl(executor);
	}

	@Override
	public void execute(Request request) throws IllegalStateException {
		dispatcher.execute(request);
	}

	@Override
	public void shutdown() {
		dispatcher.shutdown();
	}

	@Override
	public void schedule(Request request, long repeatInterval) {
		dispatcher.schedule(request, repeatInterval);
	}

	@Override
	public void scheduleOnce(Request request, long delay) {
		dispatcher.scheduleOnce(request, delay);
	}

	@Override
	public boolean isTerminated() {
		return dispatcher.isTerminated();
	}

}
