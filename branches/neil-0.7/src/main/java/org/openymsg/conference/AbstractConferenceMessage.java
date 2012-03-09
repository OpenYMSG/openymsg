package org.openymsg.conference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.openymsg.Conference;
import org.openymsg.Contact;
import org.openymsg.execute.write.Message;
import org.openymsg.network.PacketBodyBuffer;

public abstract class AbstractConferenceMessage implements Message {

	private ConferenceMembership membership;
	private Conference conference;
	private String username;

	public AbstractConferenceMessage(String username, Conference conference, ConferenceMembership membership) {
		this.username = username;
		this.conference = conference;
		this.membership = membership;
	}

	protected void writeUsername(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		body.addElement(key, this.username);
	}

	protected void writeMembers(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		for (Contact user : this.membership.getMembers()) {
			body.addElement(key, user.getId());
			// TODO - handle protocol
		}
	}

	protected void writeConference(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		body.addElement(key, this.conference.getId());
	}

}