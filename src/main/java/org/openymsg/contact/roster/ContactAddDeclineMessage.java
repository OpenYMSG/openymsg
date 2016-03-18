package org.openymsg.contact.roster;

import org.openymsg.YahooContact;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

import java.io.IOException;

public class ContactAddDeclineMessage implements Message {
	private final YahooContact contact;
	private String username;
	private final String message;

	public ContactAddDeclineMessage(String username, YahooContact contact, String message) {
		this.username = username;
		this.contact = contact;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("5", this.contact.getName());
		// TODO elements are different, how to handle MSN
		// body.addElement("241", this.contact.getProtocol().getStringValue());
		body.addElement("13", "2");// Reject Authorization
		body.addElement("97", "1");
		// TODO if no message?
		if (message != null)
			body.addElement("14", message);
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.Y7_BUDDY_AUTHORIZATION;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}
}
