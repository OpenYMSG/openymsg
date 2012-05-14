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
	private SessionConfig config;
	private SessionConnectionImpl session;

	public ConnectionInitalize(SessionConfig config, SessionConnectionImpl session) {
		this.config = config;
		this.session = session;
	}

	@Override
	public void execute() {
		// TODO - reconnect, reuse ?
		ConnectionBuilder builder = config.getConnectionBuilder();
		ConnectionHandler connection = builder.useCapacityServers().useScsServers().build();
		// TODO - null
		connection.addListener(session);
		ConnectionInfo status = builder.getConnectionInfo();
		if (status.isConnected()) {
			// TODO all one method
			this.session.initializeConnection(connection);
			this.session.setState(ConnectionState.CONNECTED, status);
		} else {
			this.session.setState(ConnectionState.FAILED_CONNECTING, status);
		}
	}

	@Override
	public void failure(Exception ex) {
		log.warn("Failure, setting ConnectionState to: " + ConnectionState.FAILED_CONNECTING, ex);
		this.session.setState(ConnectionState.FAILED_CONNECTING);
	}

}
