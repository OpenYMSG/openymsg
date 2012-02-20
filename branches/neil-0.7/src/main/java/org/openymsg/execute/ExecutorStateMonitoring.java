package org.openymsg.execute;

import org.openymsg.network.ConnectionHandlerStatus;

public interface ExecutorStateMonitoring {
	 ExecutorState getState();
	 ConnectionHandlerStatus getConnectionStatus();
	 void addListener(ExecutorStateListener listener);
	 void removeListener(ExecutorStateListener listener);

}
