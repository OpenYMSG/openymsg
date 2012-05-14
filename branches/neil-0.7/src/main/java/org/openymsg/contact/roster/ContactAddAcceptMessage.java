package org.openymsg.contact.roster;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class ContactAddAcceptMessage implements Message {
	private final YahooContact contact;
	private String username;

	public ContactAddAcceptMessage(String username, YahooContact contact) {
		this.username = username;
		this.contact = contact;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("5", this.contact.getName());
		if (!this.contact.getProtocol().isYahoo()) {
			body.addElement("241", this.contact.getProtocol().getStringValue());
		}
		body.addElement("13", "1");// Accept Authorization
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
