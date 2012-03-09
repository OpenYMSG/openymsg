package org.openymsg.conference;

import org.openymsg.Conference;
import org.openymsg.Contact;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming CONFDECLINE packet. We get one of these when someone we previously invited to a conference,
 * declines our invite.
 */
public class ConferenceDeclineResponse extends AbstractConferenceResponse {

	public ConferenceDeclineResponse(SessionConferenceCallback sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		// YahooConference yc = getOrCreateConference(pkt);
		Conference conference = this.getConference(packet);
		String to = packet.getValue("1");
		String from = packet.getValue("54");
		Contact contact = new Contact(from); // TODO protocol
		String message = packet.getValue("14");
		sessionConference.conferenceDeclineReceived(conference, contact, message);
		// Create event
		// SessionConferenceDeclineInviteEvent se = new SessionConferenceDeclineInviteEvent(this, to, from, message,
		// yc);
		// Remove the user
		// yc.removeUser(se.getFrom());
		// Fire invite event
		// if (!yc.isClosed()) // Should never be closed!
		// eventDispatchQueue.append(se, ServiceType.CONFDECLINE);

	}
}
