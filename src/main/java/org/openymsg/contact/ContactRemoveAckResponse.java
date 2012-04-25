package org.openymsg.contact;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class ContactRemoveAckResponse implements SinglePacketResponse {
	private SessionRosterImpl sessionRoster;
	private SessionGroupImpl sessionGroup;

	public ContactRemoveAckResponse(SessionRosterImpl sessionRoster, SessionGroupImpl sessionGroup) {
		this.sessionRoster = sessionRoster;
		this.sessionGroup = sessionGroup;
	}

	@Override
	public void execute(YMSG9Packet packet) {
		String me = packet.getValue("1");
		String contactUsername = packet.getValue("7");
		String groupName = packet.getValue("65");
		String removeStatus = packet.getValue("66"); // TODO what is this: 40 - failed MSN, 0 success yahoo,
		String protocol = packet.getValue("241");

		YahooProtocol yahooProtocol = YahooProtocol.YAHOO;
		if (protocol != null) {
			yahooProtocol = YahooProtocol.getProtocolOrDefault(protocol, contactUsername);
		}
		YahooContact contact = new YahooContact(contactUsername, yahooProtocol);
		if ("0".equals(removeStatus)) {
		} else {
			// failed removing contact
			return;
		}
		ContactGroupImpl group = new ContactGroupImpl(groupName);
		group.add(contact);

		boolean contactWasRemoved = sessionRoster.possibleRemoveContact(contact);
		boolean groupWasRemoved = sessionGroup.possibleRemoveGroup(group);
		// log.info("Contact was added: " + contactWasAdded + " along with the group: " + groupWasAdded);

	}

}