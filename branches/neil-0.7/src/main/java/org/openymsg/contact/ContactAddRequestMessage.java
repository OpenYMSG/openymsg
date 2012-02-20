package org.openymsg.contact;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.execute.Message;
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
	private final String groupId;

	public ContactAddRequestMessage(String username, Contact contact, String groupId) {
		this.username = username;
		this.contact = contact;
		this.groupId = groupId;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", username); // ???: effective id?
        body.addElement("302", "319");
        body.addElement("300", "319");
        body.addElement("7", this.contact.getId());
        body.addElement("241", "" + this.contact.getProtocol().getValue()); // type
        body.addElement("301", "319");
        body.addElement("303", "319");
        body.addElement("65", groupId);
        body.addElement("14", "");
        body.addElement("216", "");
        body.addElement("254", "");
        body.addElement("97", "1");
        // body.addElement("334", "0"); not used in 16
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

	@Override
	public void messageProcessed() {
	}

}
