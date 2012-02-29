package org.openymsg.execute.dispatch;


public interface Dispatcher {

	void execute(Request request);

	void schedule(Request request, long repeatInterval);

	void shutdown();

}