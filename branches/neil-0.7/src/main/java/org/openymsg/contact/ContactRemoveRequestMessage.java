package org.openymsg.contact;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a FRIENDREMOVE packet. We should get a FRIENDREMOVE packet back (usually preceded by a CONTACTNEW packet).
 * <p>
 * Note that removing a user from all groups that it is in, equals removing the user from the contact list completely.
 * @param friendId Yahoo IDof the contact to remove from a group.
 * @param groupId Group to remove the contact from.
 * @throws IllegalArgumentException if one of the arguments is null or an empty String.
 * @throws IllegalStateException if this session is not logged onto the Yahoo network correctly.
 * @throws IOException on any problem while trying to create or send the packet.
 */
public class ContactRemoveRequestMessage implements Message {

	private final String username;
	private final Contact contact;
	private final ContactGroup group;

	public ContactRemoveRequestMessage(String username, Contact contact, ContactGroup group) {
		this.username = username;
		this.contact = contact;
		this.group = group;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username); // ???: effective id?
		body.addElement("7", this.contact.getId());
		// TODO - handle protocol
		body.addElement("241", "0"); // for ack
		body.addElement("65", this.group.getName());
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.FRIENDREMOVE;
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
