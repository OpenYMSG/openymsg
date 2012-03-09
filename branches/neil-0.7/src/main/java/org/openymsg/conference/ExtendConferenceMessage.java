package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.Conference;
import org.openymsg.Contact;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFADDINVITE packet. We send one of these when we wish to invite more users to our conference.
 */
public class ExtendConferenceMessage extends AbstractConferenceMessage {
	private Contact contact;
	private String message;

	public ExtendConferenceMessage(String username, Conference conference, ConferenceMembership membership,
			Contact contact, String message) {
		super(username, conference, membership);
		this.contact = contact;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		body.addElement("51", this.contact.getId());
		// TODO - handle protocol
		this.writeConference(body, "57");
		this.writeMembers(body, "53");
		body.addElement("58", message); // TODO - if null
		body.addElement("13", "0"); // FIX : what's this for?
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFADDINVITE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	// @Override
	// public void messageProcessed() {
	// //TODO - add they are invited?
	// }

}
