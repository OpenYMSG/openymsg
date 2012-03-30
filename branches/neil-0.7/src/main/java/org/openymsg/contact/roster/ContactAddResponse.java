package org.openymsg.contact.roster;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Contact;
import org.openymsg.Name;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

/**
 * packet.status: 1 - Authorization Accepted 2 - Authorization Denied 3 - Authorization Request
 */
public class ContactAddResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(ContactAddResponse.class);
	private SessionRosterCallback sessionContact;

	public ContactAddResponse(SessionRosterCallback sessionContact) {
		this.sessionContact = sessionContact;
	}

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
		String fname = packet.getValue("216");
		String lname = packet.getValue("254");
		Name contactName = null;
		if (fname != null || lname != null) {
			contactName = new Name(fname, lname);
		}
		String id = packet.getValue("5");
		String authStatus = packet.getValue("13");
		String protocolString = packet.getValue("241");
		protocol = YahooProtocol.getProtocolOrDefault(protocolString, who);
		Contact contact = new Contact(who, protocol);
		if (packet.status == 1) {
			if (authStatus.equals("1")) {
				log.trace("A friend accepted our authorization request: " + contact);
				sessionContact.contactAddAccepted(contact);
			}
			else if (authStatus.equals("2")) {
				log.trace("A friend refused our subscription request: " + contact);
				sessionContact.contactAddDeclined(contact, message);
			}
			else {
				log.error("Unexpected authorization packet. Do not know how to handle: " + packet);
			}
		}
		else if (packet.status == 3) {
			log.trace("Someone is sending us a subscription request: " + contact);
			sessionContact.contactAddRequest(contact, contactName, message);
		}
		else {
			log.error("Unexpected authorization packet. Do not know how to handle: " + packet);
		}

	}
}
