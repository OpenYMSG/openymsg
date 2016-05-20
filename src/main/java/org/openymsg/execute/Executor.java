package org.openymsg.execute;

import org.openymsg.execute.dispatch.Request;

public interface Executor {
	void execute(Request request) throws IllegalStateException;

	void schedule(Request request, long repeatInterval) throws IllegalStateException;

	void scheduleOnce(Request request, long delay) throws IllegalStateException;

	void shutdown();

	boolean isTerminated();
}
