package org.openymsg.connection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.SessionConfig;
import org.openymsg.execute.Executor;

public class SessionConnectionImpl implements SessionConnection {
	private Executor executor;
	private ConnectionState state;
	private ConnectionInfo status;
	private Set<SessionConnectionCallback> listeners = Collections
			.synchronizedSet(new HashSet<SessionConnectionCallback>());

	public SessionConnectionImpl(Executor executor, SessionConfig sessionConfig) {
		this.executor = executor;
		this.state = ConnectionState.UNSTARTED;
		this.initialize(sessionConfig);
	}

	private void initialize(SessionConfig sessionConfig) {
		//TODO - maybe needed for restart?
		// ConnectionState state = this.getConnectionState();
		// if (state.isStartable()) {
		this.setState(ConnectionState.CONNECTING);
		this.executor.execute(new ConnectionInitalize(sessionConfig, executor, this));
		// }
		// else {
		// throw new IllegalStateException("Don't call initalize when state is: " + state);
		// }
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
}
