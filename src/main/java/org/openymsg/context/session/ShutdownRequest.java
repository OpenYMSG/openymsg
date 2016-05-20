package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.dispatch.Request;

public class ShutdownRequest implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ShutdownRequest.class);
	private final YahooConnection connection;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connection == null) ? 0 : connection.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ShutdownRequest))
			return false;
		ShutdownRequest other = (ShutdownRequest) obj;
		if (connection == null) {
			if (other.connection != null)
				return false;
		} else if (!connection.equals(other.connection))
			return false;
		return true;
	}
}
