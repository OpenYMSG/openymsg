package org.openymsg.connection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	private Set<SessionConnectionCallback> listeners = Collections
			.synchronizedSet(new HashSet<SessionConnectionCallback>());

	public SessionConnectionImpl(Executor executor) {
		this.executor = executor;
		this.state = ConnectionState.UNSTARTED;
	}

	/**
	 * initialize the connection based on the config. Config options should not change
	 * @param sessionConfig configuration
	 */
	public void initialize(SessionConfig sessionConfig) {
		//TODO - maybe needed for restart?
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

	@Override
	public void addListener(SessionConnectionCallback listener) {
		this.listeners.add(listener);
		callCallback(listener);
	}

	@Override
	public boolean removeListener(SessionConnectionCallback listener) {
		return this.listeners.remove(listener);
	}

	protected void setState(ConnectionState state) {
		this.state = state;
		synchronized (listeners) {
			for (SessionConnectionCallback listener : listeners) {
				callCallback(listener);
			}
		}
	}

	protected void setState(ConnectionState state, ConnectionInfo status) {
		this.status = status;
		this.setState(state);
	}

	private void callCallback(SessionConnectionCallback listener) {
		if (this.state == ConnectionState.CONNECTED) {
			listener.connectionSuccessful();
		}
		else if (this.state == ConnectionState.FAILED_CONNECTING) {
			listener.connectionFailure();
		}
		else if (this.state == ConnectionState.FAILED_AFTER_CONNECTED) {
			listener.connectionPrematurelyEnded();
		}
	}

	@Override
	public void connectionEnded() {
		if (this.state == ConnectionState.CONNECTED) {
			this.setState(ConnectionState.FAILED_AFTER_CONNECTED);
		}
		else {
			log.warn("got connection ended with state: " + this.state);
		}
	}
}
