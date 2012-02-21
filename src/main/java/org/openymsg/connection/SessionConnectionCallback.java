package org.openymsg.connection;

/** Notification of Connection changes */
public interface SessionConnectionCallback {
	/**
	 * Connection was successfully connected
	 */
	void connectionSuccessful();

	/**
	 * Connection failed connecting. Check the ConnectionInfo for information about the failure
	 */
	void connectionFailure();

	/**
	 * Connection failed prematurely. This is likely the result of a SocketException
	 */
	// TODO somehow pass the exception?
	void connectionPrematurelyEnded();
}
