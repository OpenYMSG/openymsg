package org.openymsg.auth;

import org.openymsg.execute.Executor;
import org.openymsg.execute.Request;

public class ShutdownRequest implements Request {
	Executor dispatcher;
	
	public ShutdownRequest(Executor dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void run() {
		this.dispatcher.shutdown();
	}

}
