package org.openymsg.conference.response;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.SessionConferenceCallback;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming CONFLOGON packet. We get one of these when someone we
 * previously invited to a conference, accepts our invite.
 */
public class ConferenceAcceptResponse extends AbstractConferenceResponse {

	public ConferenceAcceptResponse(SessionConferenceCallback sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		YahooConference conference = this.getConference(packet);
		@SuppressWarnings("unused")
		String to = packet.getValue("1");
		String from = packet.getValue("53");
		YahooContact contact = new YahooContact(from, YahooProtocol.YAHOO); // TODO
																			// protocol
		sessionConference.receivedConferenceAccept(conference, contact);
		// if (!yc.isClosed()) // Should never be closed!
	}
}
