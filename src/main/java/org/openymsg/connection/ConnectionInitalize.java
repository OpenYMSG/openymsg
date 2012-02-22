package org.openymsg.connection;

import org.openymsg.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.execute.Request;
import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.ConnectionHandler;

public class ConnectionInitalize implements Request {
	private SessionConfig config;
	private SessionConnectionImpl monitor;
	private Executor executor;
	
	public ConnectionInitalize(SessionConfig config, Executor executor, SessionConnectionImpl monitor) {
		this.config = config;
		this.executor = executor;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		ConnectionBuilder builder = config.getBuilder();
		ConnectionHandler connection = builder.useCapacityServers().useScsServers().build();
		connection.addListener(monitor);
		ConnectionInfo status = builder.getHandlerStatus();
		if (status.isConnected()) {
			this.executor.initializeConnection(connection);
			this.monitor.setState(ConnectionState.CONNECTED, status);
		} else {
			this.monitor.setState(ConnectionState.FAILED_CONNECTING, status);
		}
	}

}
