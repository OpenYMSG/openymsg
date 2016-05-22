package org.openymsg.conference.message;

import java.io.IOException;
import java.util.Set;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.conference.ConferenceMembership;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFADDINVITE packet. We send one of these when we wish to invite
 * more users to our conference.
 */
public class ExtendConferenceMessage extends AbstractConferenceMessage {
	private final Set<YahooContact> invitedContacts;
	private final String message;

	public ExtendConferenceMessage(String username, YahooConference conference, ConferenceMembership membership,
			Set<YahooContact> invitedContacts, String message) {
		super(username, conference, membership);
		this.invitedContacts = invitedContacts;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		this.writeConference(body, "57");
		StringBuffer buffer = new StringBuffer();
		for (YahooContact contact : this.invitedContacts) {
			buffer.append(contact.getName());
			buffer.append(",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		body.addElement("51", buffer.toString());
		// TODO - handle protocol
		this.writeMembers(body, "53");
		this.writeInvited(body, "52");
		body.addElement("58", message); // TODO - if null
		body.addElement("97", "1"); // TODO - if messages is null?
		body.addElement("13", "0"); // what's this for?
		// this.writeConference(body, "234");
		// body.addElement("233", "some key"); // what's this for?
		// TODO
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

	@Override
	public String toString() {
		return String.format(
				"ExtendConferenceMessage [invitedContacts=%s, message=%s, membership=%s, conference=%s, username=%s]",
				invitedContacts, message, membership, conference, username);
	}
}
