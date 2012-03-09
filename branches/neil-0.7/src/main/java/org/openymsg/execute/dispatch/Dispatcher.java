package org.openymsg.execute.dispatch;

public interface Dispatcher {

	void execute(Request request) throws IllegalStateException;

	void schedule(Request request, long repeatInterval) throws IllegalStateException;

	void shutdown();

}