package org.openymsg.network;

/**
 * Handler of notification of Connection changes
 * @author neilhart
 */
public interface ConnectionHandlerCallback {

	/**
	 * Connection has ended.
	 */
	void connectionEnded(ConnectionEndedReason reason);
}
