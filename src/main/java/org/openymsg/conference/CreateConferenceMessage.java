package org.openymsg.conference;

import java.io.IOException;
import java.util.Set;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFINVITE packet. This is sent when we want to create a new conference, with the specified users and
 * with a given welcome message.
 */
public class CreateConferenceMessage implements Message {
	private String username;
	private YahooConference conference;
	private Set<YahooContact> contacts;
	private String message;

	public CreateConferenceMessage(String username, YahooConference conference, Set<YahooContact> contacts,
			String message) {
		this.username = username;
		this.conference = conference;
		this.contacts = contacts;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("50", this.username); // TODO not sure why
		body.addElement("57", this.conference.getId());
		body.addElement("58", this.message);
		body.addElement("97", "1");
		for (YahooContact contact : this.contacts) {
			body.addElement("52", contact.getName());
			// TODO - handle protocol
		}
		body.addElement("13", "0"); // 0 for not voice. voice is 256
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFINVITE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	// @Override
	// public void messageProcessed() {
	// // TODO - add conference
	// // Create a new conference object
	// // conferences.put(room, new YahooConference(identities.get(yid.toLowerCase()), room, msg, this, false));
	// // Send request to Yahoo
	// }

}
