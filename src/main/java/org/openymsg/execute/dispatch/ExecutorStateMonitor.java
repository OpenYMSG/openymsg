package org.openymsg.execute.dispatch;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.execute.ExecutorState;
import org.openymsg.execute.ExecutorStateListener;
import org.openymsg.execute.ExecutorStateMonitoring;
import org.openymsg.network.ConnectionHandlerStatus;

public class ExecutorStateMonitor implements ExecutorStateMonitoring {
	private ExecutorState state;
	private ConnectionHandlerStatus status;
	private Set<ExecutorStateListener> listeners = new HashSet<ExecutorStateListener>();
	
	public ExecutorStateMonitor() {
		this.state = ExecutorState.UNSTARTED;
	}

	public void setState(ExecutorState state, ConnectionHandlerStatus status) {
		this.state = state;
		this.status = status;
		for (ExecutorStateListener listener : listeners) {
			listener.setState(state);
		}
	}
	
	@Override
	public ExecutorState getState() {
		return state;
	}

	@Override
	public ConnectionHandlerStatus getConnectionStatus() {
		return status;
	}

	@Override
	public void addListener(ExecutorStateListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(ExecutorStateListener listener) {
		this.listeners.remove(listener);
	}

}
