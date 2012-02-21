package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.Conference;
import org.openymsg.Contact;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFADDINVITE packet. We send one of these when we wish to invite more users to our conference.
 */
public class ExtendConferenceMessage implements Message {
	private String username;
	private Conference conference;
	private Contact contact;
	private String message;

	public ExtendConferenceMessage(String username, Conference conference, Contact contact, String message) {
		this.username = username;
		this.conference = conference;
		this.contact = contact;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", this.username);
        body.addElement("51", this.contact.getId());
        //TODO - handle protocol
        body.addElement("57", this.conference.getId());
        for (Contact user : this.conference.getMembers()) {
            body.addElement("53", user.getId());
            //TODO - handle protocol
        }
        body.addElement("58", message);
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

	@Override
	public void messageProcessed() {
		//TODO - add they are invited?
	}

}
