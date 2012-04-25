package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.YahooConference;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFDECLINE packet. We send one of these when we decline an offer to join a conference.
 */
public class DeclineConferenceMessage extends AbstractConferenceMessage {
	private String message;

	public DeclineConferenceMessage(String username, YahooConference conference, ConferenceMembership membership,
			String message) {
		super(username, conference, membership);
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		this.writeMembers(body, "3");
		this.writeConference(body, "57");
		// TODO - if not null?
		if (this.message != null) {
			body.addElement("14", this.message);
		}
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFDECLINE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	// @Override
	// public void messageProcessed() {
	// // TODO - remove conference
	// // Flag this conference as now dead
	// // YahooConference yc = getConference(room);
	// // yc.closeConference();
	// }

}
