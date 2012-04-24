package org.openymsg.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.network.ConnectionHandlerCallback;

public class SessionConnectionImpl implements SessionConnection, ConnectionHandlerCallback {
	private static final Log log = LogFactory.getLog(SessionConnectionImpl.class);
	private Executor executor;
	private ConnectionState state;
	private ConnectionInfo status;
	private SessionConnectionCallback callback;

	public SessionConnectionImpl(Executor executor, SessionConnectionCallback callback) {
		this.executor = executor;
		this.callback = callback;
		this.state = ConnectionState.UNSTARTED;
	}

	/**
	 * initialize the connection based on the config. Config options should not change
	 * @param sessionConfig configuration
	 */
	public void initialize(SessionConfig sessionConfig) throws IllegalArgumentException {
		if (sessionConfig == null) {
			throw new IllegalArgumentException("sessionConfig must not be null");
		}
		// TODO - maybe needed for restart?
		// ConnectionState state = this.getConnectionState();
		// if (state.isStartable()) {
		this.setState(ConnectionState.CONNECTING);
		this.executor.execute(new ConnectionInitalize(sessionConfig, executor, this));
	}

	@Override
	public ConnectionState getConnectionState() {
		return state;
	}

	@Override
	public ConnectionInfo getConnectionInfo() {
		return status;
	}

	protected void setState(ConnectionState state) {
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

	@Override
	public void connectionEnded() {
		if (this.state != ConnectionState.DISCONNECTING) {
			this.setState(ConnectionState.FAILED_AFTER_CONNECTED);
		} else {
			log.warn("got connection ended with state: " + this.state);
		}
	}

	public void closeConnection() {
		this.state = ConnectionState.DISCONNECTING;
		this.executor.shutdown();
	}
}
