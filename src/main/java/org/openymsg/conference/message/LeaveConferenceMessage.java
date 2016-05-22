package org.openymsg.conference.message;

import java.io.IOException;

import org.openymsg.YahooConference;
import org.openymsg.conference.ConferenceMembership;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFLOGOFF packet. We send one of these when we wish to leave a
 * conference.
 */
public class LeaveConferenceMessage extends AbstractConferenceMessage {
	public LeaveConferenceMessage(String username, YahooConference conference, ConferenceMembership membership) {
		super(username, conference, membership);
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		this.writeConference(body, "57");
		// TODO merge sets to avoid dups
		this.writeMembers(body, "3");
		this.writeInvited(body, "3");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFLOGOFF;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	@Override
	public String toString() {
		return String.format("LeaveConferenceMessage [membership=%s, conference=%s, username=%s]", membership,
				conference, username);
	}
}
