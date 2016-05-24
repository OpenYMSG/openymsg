package org.openymsg.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.ConnectionHandler;

public class ConnectionInitalize implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ConnectionInitalize.class);
	private final SessionConfig config;
	private ConnectionStateAndDetails connectionState;

	public ConnectionInitalize(SessionConfig config, ConnectionStateAndDetails connectionState) {
		this.config = config;
		this.connectionState = connectionState;
	}

	@Override
	public void execute() {
		// TODO - reconnect, reuse ?
		ConnectionBuilder builder = config.getConnectionBuilder();
		ConnectionHandler connection = builder.useCapacityServers().useScsServers().build();

		if (connection == null) {
			this.connectionState.setState(ConnectionState.FAILED_CONNECTING);
			return;
		}
		connection.addListener(connectionState);

		ConnectionInfo status = builder.getConnectionInfo();
		if (status.isConnected()) {
			this.connectionState.setConnected(connection, status);
		} else {
			this.connectionState.setState(ConnectionState.FAILED_CONNECTING, status);
		}
	}

	@Override
	public void failure(Exception ex) {
		log.warn("Failure, setting ConnectionState to: " + ConnectionState.FAILED_CONNECTING, ex);
		this.connectionState.setState(ConnectionState.FAILED_CONNECTING);
		// TODO shutdown
	}

	@Override
	public String toString() {
		return String.format("ConnectionInitalize [connectionState=%s]", connectionState);
	}
}
