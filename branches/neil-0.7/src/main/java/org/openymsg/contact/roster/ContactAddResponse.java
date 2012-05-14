package org.openymsg.contact.roster;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

/**
 * packet.status: 1 - Authorization Accepted 2 - Authorization Denied 3 - Authorization Request
 */
public class ContactAddResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(ContactAddResponse.class);
	private SessionRosterImpl sessionRoster;

	public ContactAddResponse(SessionRosterImpl sessionRoster) {
		this.sessionRoster = sessionRoster;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		// TODO - check this with all
		if (packet.length <= 0) {
			return;
		}

		YahooProtocol protocol;
		String who = packet.getValue("4");
		String message = packet.getValue("14");
		// TODO - UTF8
		@SuppressWarnings("unused")
		String me = packet.getValue("5");
		String protocolString = packet.getValue("241");
		if (protocolString == null) {
			protocol = YahooProtocol.YAHOO;
		} else {
			protocol = YahooProtocol.getProtocolOrDefault(protocolString, who);
		}
		YahooContact contact = new YahooContact(who, protocol);
		if (packet.status == 1) {
			String authStatus = packet.getValue("13");
			if (authStatus.equals("1")) {
				log.trace("A friend accepted our authorization request: " + contact);
				sessionRoster.receivedContactAddAccepted(contact);
			} else if (authStatus.equals("2")) {
				log.trace("A friend refused our subscription request: " + contact);
				sessionRoster.receivedContactAddDeclined(contact, message);
			} else {
				log.error("Unexpected authorization packet. Do not know how to handle: " + packet);
			}
		} else if (packet.status == 3) {
			Name contactName = null;
			String fname = packet.getValue("216");
			String lname = packet.getValue("254");
			if (fname != null || lname != null) {
				contactName = new Name(fname, lname);
			}
			log.trace("Someone is sending us a subscription request: " + contact);
			sessionRoster.receivedContactAddRequest(contact, contactName, message);
		} else {
			log.error("Unexpected authorization packet. Do not know how to handle: " + packet);
		}

	}
}
