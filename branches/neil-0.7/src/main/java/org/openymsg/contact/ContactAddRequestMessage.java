package org.openymsg.contact;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a FRIENDADD packet. If all goes well we'll get a FRIENDADD packet back with the details of the friend to
 * confirm the transaction (usually preceded by a CONTACTNEW packet with well detailed info).
 * 
 * @param userId
 *            Yahoo id of friend to add group
 * @param groupId
 *            Group to add the new friend in
 * @throws IllegalArgumentException
 *             if one of the arguments is null or an empty String.
 * @throws IllegalStateException
 *             if this session is not logged onto the Yahoo network correctly.
 * @throws IOException
 *             on any problem while trying to create or send the packet.
 */
public class ContactAddRequestMessage implements Message {

	private final String username;
	private final Contact contact;
	private final ContactGroup group;

	public ContactAddRequestMessage(String username, Contact contact, ContactGroup group) {
		this.username = username;
		this.contact = contact;
		this.group = group;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("14", ""); // C0 80 - TODO message
        body.addElement("65", this.group.getName());
        body.addElement("97", "1"); //TODO - UNICODE?
        body.addElement("216", ""); //first name TODO
        body.addElement("254", ""); //last name TODO
        body.addElement("1", username);
        body.addElement("302", "319");
        body.addElement("300", "319");
        body.addElement("7", this.contact.getId());
        if (!this.contact.getProtocol().isYahoo()) {
        	body.addElement("241", "" + this.contact.getProtocol().getValue()); // type
        }
        body.addElement("301", "319");
        body.addElement("303", "319");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.FRIENDADD;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
