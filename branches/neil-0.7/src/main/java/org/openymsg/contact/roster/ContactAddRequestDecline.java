package org.openymsg.contact.roster;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class ContactAddRequestDecline implements Message {
	private final Contact contact;
	private String username;
	private final String message;

	public ContactAddRequestDecline(String username, Contact contact, String message) {
		this.username = username;
		this.contact = contact;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("7", this.contact.getId());
		// TODO elements are different, how to handle MSN
		// body.addElement("5", this.contact.getId());
		// body.addElement("241", this.contact.getProtocol().getStringValue());
		body.addElement("13", "2");// Reject Authorization
		if (message != null) body.addElement("14", message);

		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.Y7_AUTHORIZATION;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
