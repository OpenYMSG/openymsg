package org.openymsg.execute.dispatch;

import org.openymsg.SessionConfig;
import org.openymsg.execute.ExecutorState;
import org.openymsg.execute.Request;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ConnectionHandlerStatus;
import org.openymsg.network.direct.DirectConnectionBuilder;

public class ConnectionInitalize implements Request {
	private SessionConfig config;
	private ExecutorStateMonitor monitor;
	private ExecutorImpl executor;
	
	public ConnectionInitalize(SessionConfig config, ExecutorImpl executor, ExecutorStateMonitor monitor) {
		this.config = config;
		this.executor = executor;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		DirectConnectionBuilder builder = new DirectConnectionBuilder();
		ConnectionHandler connection = builder.with(config).useCapacityServers().useScsServers().build();
		ConnectionHandlerStatus status = builder.getHandlerStatus();
		if (status.isConnected()) {
			this.executor.setConnection(connection);
			this.monitor.setState(ExecutorState.CONNECTED, status);
		} else {
			this.monitor.setState(ExecutorState.FAILED, status);
		}
	}

}
