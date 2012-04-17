package org.openymsg.session;

import java.io.IOException;

import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmits a Keep-Alive packet to the Yahoo network. At the time of implementation, this packet gets sent by the
 * official Yahoo client once every 60 seconds, and seems to replace the older PING service type (although this service
 * type is still received by the client just after authentication). The keep-alive does not appear to be sent to a Yahoo
 * Chatrooms.
 * @throws IOException
 */
public class KeepAliveMessage implements Message {
	private String username;

	public KeepAliveMessage(String username) {
		this.username = username;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", username);
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.KEEPALIVE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
