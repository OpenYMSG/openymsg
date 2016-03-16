package org.openymsg.network;

/**
 * The connection to communicate to Yahoo Subclasses of this will need to maintain the sessionId. Subclasses of this may
 * not be thread-safe.
 * @author neilhart
 */
public interface ConnectionHandler {

	/**
	 * Send a message to Yahoo containing the following information. This may not be thread-safe.
	 * @param body body of the message
	 * @param service type of service of the message
	 * @param status type of status of the message
	 */
	void sendPacket(PacketBodyBuffer body, ServiceType service, MessageStatus status);

	/**
	 * Return a Yahoo message or null. Does not wait. This may not be thread-safe
	 */
	YMSG9Packet receivePacket();

	/**
	 * Shutdown the connection
	 */
	// TODO Must call
	void shutdown();

	/**
	 * Add a listener
	 * @param listener
	 */
	void addListener(ConnectionHandlerCallback listener);

	/**
	 * Remove a listener
	 * @param listener
	 */
	void removeListener(ConnectionHandlerCallback listener);

	boolean isDisconnected();

	boolean isLocked(int millisDuration);
}
