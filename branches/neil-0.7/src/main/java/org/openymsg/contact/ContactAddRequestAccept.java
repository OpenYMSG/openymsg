package org.openymsg.contact;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class ContactAddRequestAccept implements Message {
	private final Contact contact;
	private String username;

	public ContactAddRequestAccept(String username, Contact contact) {
		this.username = username;
		this.contact = contact;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", this.username);
        body.addElement("5", this.contact.getId());
        body.addElement("241", this.contact.getProtocol().getStringValue());
        body.addElement("13", "1");// Accept Authorization
        // body.addElement("334", ""); not therein v16
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

	@Override
	public void messageProcessed() {
		//TODO add contact
	}

}
