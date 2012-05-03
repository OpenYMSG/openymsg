package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.Request;

public class ShutdownRequest implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ShutdownRequest.class);
	Executor dispatcher;

	public ShutdownRequest(Executor dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void execute() {
		this.dispatcher.shutdown();
	}

	@Override
	public void failure(Exception ex) {
		log.error("failed shutting down", ex);
	}

}
