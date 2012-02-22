package org.openymsg.network;

import org.openymsg.network.YMSG9Packet;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public interface ConnectionHandler {
	/**
	 * The connection Handler is responsible for having the session id
	 * 
	 * @param body
	 * @param service
	 * @param status
	 */
	void sendPacket(PacketBodyBuffer body, ServiceType service, MessageStatus status);

	/**
	 * Return a Yahoo message or null. Does not wait
	 */
	YMSG9Packet receivePacket();

	void shutdown();
	
	void addListener(ConnectionHandlerCallback listener);
}
