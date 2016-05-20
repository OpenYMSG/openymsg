package org.openymsg.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Executor;
import org.openymsg.network.ConnectionEndedReason;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ConnectionHandlerCallback;

public class ConnectionStateAndDetails implements ConnectionHandlerCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(ConnectionStateAndDetails.class);
	private final SessionConnectionCallback callback;
	private final ConnectionService writerService;
	private final ConnectionService readerService;
	private final Executor executor;
	private ConnectionState state;
	private ConnectionInfo status;
	private ConnectionHandler connection;

	public ConnectionStateAndDetails(Executor executor, ConnectionService writerService,
			ConnectionService readerService, SessionConnectionCallback callback) {
		this.executor = executor;
		this.writerService = writerService;
		this.readerService = readerService;
		this.callback = callback;
	}

	@Override
	public void connectionEnded(ConnectionEndedReason reason) {
		// TODO No Reason
		if (this.state != ConnectionState.DISCONNECTING) {
			this.setState(ConnectionState.FAILED_AFTER_CONNECTED);
			this.shutdown();
		} else {
			log.warn("got connection ended with state: " + this.state);
		}
	}

	// @Override
	public void shutdown() {
		log.info("Shutting down everything");
		this.setDisconnecting();
		this.readerService.stop();
		this.writerService.stop();
		this.connection.shutdown();
		this.executor.shutdown();
	}

	public void setUnstarted() {
		state = ConnectionState.UNSTARTED;
	}

	public ConnectionState getState() {
		return state;
	}

	public ConnectionInfo getInfo() {
		return status;
	}

	public void setState(ConnectionState state) {
		this.state = state;
		if (this.state == ConnectionState.CONNECTED) {
			callback.connectionSuccessful();
		} else if (this.state == ConnectionState.FAILED_CONNECTING) {
			callback.connectionFailure();
		} else if (this.state == ConnectionState.FAILED_AFTER_CONNECTED) {
			callback.connectionPrematurelyEnded();
		}
	}

	protected void setState(ConnectionState state, ConnectionInfo status) {
		this.status = status;
		this.setState(state);
	}

	public void setDisconnecting() {
		state = ConnectionState.DISCONNECTING;
	}

	public void setConnecting() {
		state = ConnectionState.CONNECTING;
	}

	public void setConnected(ConnectionHandler connection, ConnectionInfo status) {
		this.connection = connection;
		this.writerService.start(connection);
		this.readerService.start(connection);
		this.setState(ConnectionState.CONNECTED, status);
	}
}
