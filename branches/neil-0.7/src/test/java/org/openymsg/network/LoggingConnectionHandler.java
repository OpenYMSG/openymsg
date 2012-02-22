package org.openymsg.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingConnectionHandler implements ConnectionHandler {
	private static final Log log = LogFactory.getLog(LoggingConnectionHandler.class);
	private int sessionId = 0;

	@Override
	public void sendPacket(PacketBodyBuffer body, ServiceType service, MessageStatus status) {
		log.info("Sent packet: Magic:YMSG Version:16 Length:" + body.getBuffer().length + " Service:" + service
				+ " Status:" + status + " SessionId:" + (sessionId  & 0xFFFFFFFF) + " " + body);
	}

	@Override
	public YMSG9Packet receivePacket() {
		return null;
	}

	@Override
	public void shutdown() {
		log.info("shutdown");
	}

	@Override
	public void addListener(ConnectionHandlerCallback listener) {
		log.info("no op add listener");
	}

}
