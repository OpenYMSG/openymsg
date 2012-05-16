package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.dispatch.Request;

public class ShutdownRequest implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ShutdownRequest.class);
	YahooConnection connection;

	public ShutdownRequest(YahooConnection connection) {
		this.connection = connection;
	}

	@Override
	public void execute() {
		this.connection.shutdown();
	}

	@Override
	public void failure(Exception ex) {
		log.error("failed shutting down", ex);
	}

}
