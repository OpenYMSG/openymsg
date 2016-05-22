package org.openymsg.conference.message;

import java.io.IOException;

import org.openymsg.YahooConference;
import org.openymsg.conference.ConferenceMembership;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFMSG packet. We send one of these when we wish to send a
 * message to a conference.
 */
public class SendConfereneMessage extends AbstractConferenceMessage {
	private final String message;

	public SendConfereneMessage(String username, YahooConference conference, ConferenceMembership membership,
			String message) {
		super(username, conference, membership);
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		this.writeConference(body, "57");
		this.writeMembers(body, "53");
		body.addElement("97", "1");
		body.addElement("14", message);
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFMSG;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	@Override
	public String toString() {
		return String.format("SendConfereneMessage [message=%s, membership=%s, conference=%s, username=%s]", message,
				membership, conference, username);
	}
}
