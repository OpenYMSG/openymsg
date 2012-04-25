package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.YahooConference;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFLOGON packet. Send this when we want to accept an offer to join a conference.
 */
public class AcceptConferenceMessage extends AbstractConferenceMessage {

	public AcceptConferenceMessage(String username, YahooConference conference, ConferenceMembership membership) {
		super(username, conference, membership);
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		this.writeConference(body, "57");
		this.writeMembers(body, "3");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFLOGON;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
