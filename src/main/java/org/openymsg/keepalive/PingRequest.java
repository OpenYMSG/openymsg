package org.openymsg.keepalive;

import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a PING packet. Needed every hour to keep from getting knocked off by LOGGOFF 52
 */
public class PingRequest implements Message {

	@Override
	public PacketBodyBuffer getBody() {
		PacketBodyBuffer body = new PacketBodyBuffer();
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.PING;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
