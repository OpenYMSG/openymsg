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
	 * @return
	 */
	ConnectionInfo getConnectionInfo();

	/**
	 * Register interest in changes. The callback method will be called during registration.
	 * @param listener callback for notification of changes
	 */
	void addListener(SessionConnectionCallback listener);

	/**
	 * Remove interest in changes.
	 * @param listener callback for notication of changes
	 * @return true if found and removed.
	 */
	boolean removeListener(SessionConnectionCallback listener);

}