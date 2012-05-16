package org.openymsg.connection;

/** SessionConnection for managing connecting to Yahoo */
public interface SessionConnection {
	/**
	 * Get the current ConnectionState
	 * @return current state
	 */
	ConnectionState getConnectionState();

	/**
	 * Get the ConnectionInfo. This will not change once set.
	 * @return information of the connection
	 */
	ConnectionInfo getConnectionInfo();

}