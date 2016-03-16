package org.openymsg.conference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.connection.write.Message;
import org.openymsg.network.PacketBodyBuffer;

public abstract class AbstractConferenceMessage implements Message {
	private ConferenceMembership membership;
	private YahooConference conference;
	private String username;

	public AbstractConferenceMessage(String username, YahooConference conference, ConferenceMembership membership) {
		this.username = username;
		this.conference = conference;
		this.membership = membership;
	}

	protected void writeUsername(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		body.addElement(key, this.username);
	}

	// TODO don't include me
	protected void writeMembers(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		for (YahooContact contact : this.membership.getMembers()) {
			body.addElement(key, contact.getName());
			// TODO - handle protocol
		}
	}

	protected void writeInvited(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		for (YahooContact contact : this.membership.getInvited()) {
			body.addElement(key, contact.getName());
			// TODO - handle protocol
		}
	}

	protected void writeConference(PacketBodyBuffer body, String key) throws UnsupportedEncodingException, IOException {
		body.addElement(key, this.conference.getId());
	}

}