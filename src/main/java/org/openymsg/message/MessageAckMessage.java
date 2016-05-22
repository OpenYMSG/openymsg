package org.openymsg.message;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Send acknowledgement. If we don't do this then the official Yahoo Messenger
 * client for Windows will send us the same message 7 seconds later as an
 * offline message. This is true for at least version 9.0.0.2162 on Windows XP.
 */
public class MessageAckMessage implements Message {
	private final String username;
	private final YahooContact contact;
	private final String messageId;

	public MessageAckMessage(String username, YahooContact contact, String messageId) {
		this.username = username;
		this.contact = contact;
		this.messageId = messageId;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", username);
		body.addElement("5", contact.getName());
		// TODO - protocol;
		body.addElement("302", "430");
		body.addElement("430", messageId);
		body.addElement("303", "430");
		body.addElement("450", "0");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.MESSAGE_ACK;
	}

	@Override
	public MessageStatus getMessageStatus() {
		// TODO - is this correct or use current?
		return MessageStatus.DEFAULT;
	}

	@Override
	public String toString() {
		return String.format("MessageAckMessage [username=%s, contact=%s, messageId=%s]", username, contact, messageId);
	}
}
