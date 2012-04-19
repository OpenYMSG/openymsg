package org.openymsg.conference;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming CONFMSG packet. We get one of these when someone in a conference we are part of sends a message.
 * Note: in extreme circumstances this may arrive before the invite packet.
 */
public class ConferenceMessageResponse extends AbstractConferenceResponse {

	public ConferenceMessageResponse(SessionConferenceImpl sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		YahooConference conference = this.getConference(packet);
		// If we have not received an invite yet, buffer packets
		String id = packet.getValue("57");
		String name = packet.getValue("58");
		String to = packet.getValue("1");
		String from = packet.getValue("3"); // TODO - protocol
		YahooContact contact = new YahooContact(from);
		String message = packet.getValue("14"); // unicode
		sessionConference.receivedConferenceMessage(conference, contact, message);
		// TODO add invite packet?
		// synchronized (yc) {
		// if (!yc.isInvited()) {
		// yc.addPacket(pkt);
		// return;
		// }
		// }
		// // Otherwise, handle the packet
		// try {
		// SessionConferenceMessageEvent se = new SessionConferenceMessageEvent(this, to,
		// from, message, yc);
		// // Fire event
		// if (!yc.isClosed()) eventDispatchQueue.append(se, ServiceType.CONFMSG);
		// }
		// catch (Exception e) {
		// throw new YMSG9BadFormatException("conference mesg", pkt, e);
		// }
	}

}
