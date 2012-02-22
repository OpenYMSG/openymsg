package org.openymsg.group;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class MoveContactToGroupMessage implements Message {
	private String username;
	private Contact contact;
	private ContactGroup from;
	private ContactGroup to;
	
	public MoveContactToGroupMessage(String username, Contact contact, ContactGroup from, ContactGroup to) {
		this.username = username;
		this.contact = contact;
		this.from = from;
		this.to = to;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", this.username);
        body.addElement("302", "240");
        body.addElement("300", "240");
        body.addElement("7", this.contact.getId());
        //TODO - handle protocol
        body.addElement("224", this.from.getName());
        body.addElement("264", this.to.getName());
        body.addElement("301", "240");
        body.addElement("303", "240");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.Y7_CHANGE_GROUP;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
