package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.Conference;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFLOGOFF packet. We send one of these when we wish to leave a conference.
 */
public class LeaveConferenceMessage extends AbstractConferenceMessage {

	public LeaveConferenceMessage(String username, Conference conference, ConferenceMembership membership) {
		super(username, conference, membership);
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		// Send decline packet to Yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		this.writeUsername(body, "1");
		this.writeMembers(body, "3");
		this.writeConference(body, "57");
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

	// @Override
	// public void messageProcessed() {
	// //TODO - close conference
	// // // Flag this conference as now dead
	// // YahooConference yc = getConference(room);
	// // yc.closeConference();
	// }

}
