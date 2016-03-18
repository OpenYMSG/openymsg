package org.openymsg.contact.roster;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

import java.io.IOException;

/**
 * Transmit a REMOVE_BUDDY packet. We should get a REMOVE_BUDDY packet back (usually preceded by a CONTACTNEW packet).
 * <p>
 * Note that removing a user from all groups that it is in, equals removing the user from the contact list completely.
 */
public class ContactRemoveMessage implements Message {
	private final String username;
	private final YahooContact contact;
	private final YahooContactGroup group;

	public ContactRemoveMessage(String username, YahooContact contact, YahooContactGroup group) {
		this.username = username;
		this.contact = contact;
		this.group = group;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username); // ???: effective id?
		body.addElement("7", this.contact.getName());
		// TODO - handle protocol
		body.addElement("65", this.group.getName());
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.REMOVE_BUDDY;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}
	// @Override
	// public void messageProcessed() {
	// //TODO - remove contact
	// }
}
