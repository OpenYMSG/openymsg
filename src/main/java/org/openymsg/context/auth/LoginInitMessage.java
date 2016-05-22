package org.openymsg.context.auth;

import java.io.IOException;

import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an AUTH packet, as a way of introduction to the server. As we do not
 * know our primary ID yet, both 0 and 1 use username . May need 0 for HTTP
 * connection
 */
public class LoginInitMessage implements Message {
	private final String username;

	public LoginInitMessage(String username) {
		this.username = username;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", username);
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.AUTH;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	@Override
	public String toString() {
		return String.format("LoginInitMessage [username=%s]", username);
	}
}
