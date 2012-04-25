package org.openymsg.conference;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming CONFLOGON packet. We get one of these when someone we previously invited to a conference, accepts
 * our invite.
 */
public class ConferenceLeaveResponse extends AbstractConferenceResponse {

	public ConferenceLeaveResponse(SessionConferenceImpl sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		@SuppressWarnings("unused")
		String to = packet.getValue("1");
		String value302 = packet.getValue("302"); // 3
		// multiple 3 for each in conference room
		String value303 = packet.getValue("302"); // 3
		String from = packet.getValue("56");
		YahooConference conference = this.getConference(packet);
		YahooContact contact = new YahooContact(from, YahooProtocol.YAHOO); // TODO protocol
		sessionConference.receivedConferenceLeft(conference, contact);

		// if (!yc.isClosed()) // Should never be closed!
	}
}
