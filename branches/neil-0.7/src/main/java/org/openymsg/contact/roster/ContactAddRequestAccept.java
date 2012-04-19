package org.openymsg.contact.roster;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class ContactAddRequestAccept implements Message {
	private final YahooContact contact;
	private String username;

	public ContactAddRequestAccept(String username, YahooContact contact) {
		this.username = username;
		this.contact = contact;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("5", this.contact.getName());
		body.addElement("241", this.contact.getProtocol().getStringValue()); // TODO - is this there for regular yahoo
																				// users?
		body.addElement("13", "1");// Accept Authorization
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

	// @Override
	// public void messageProcessed() {
	// //TODO add contact
	// }

}
