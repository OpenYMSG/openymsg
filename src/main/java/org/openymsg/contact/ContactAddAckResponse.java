package org.openymsg.contact;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.ContactAddFailure;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming ADD_BUDDY packet. We get one of these after we've sent a ADD_BUDDY packet, as confirmation. It
 * contains basic details of our new friend, although it seems a bit redundant as Yahoo sents a CONTACTNEW with these
 * details before this packet.
 */
public class ContactAddAckResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(ContactAddAckResponse.class);
	private SessionRosterImpl sessionRoster;
	private SessionGroupImpl sessionGroup;
	private SessionStatusImpl sessionStatus;

	public ContactAddAckResponse(SessionRosterImpl sessionRoster, SessionGroupImpl sessionGroup,
			SessionStatusImpl sessionStatus) {
		this.sessionRoster = sessionRoster;
		this.sessionGroup = sessionGroup;
		this.sessionStatus = sessionStatus;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		// TODO should we always check for this?
		if (packet.status != 1) {
			log.warn("got ContactAddAckResponse with status: " + packet.status);
			return;
		}
		String me = packet.getValue("1");
		String contactUsername = packet.getValue("7");
		String protocolString = packet.getValue("241");
		YahooProtocol protocol;
		if (protocolString == null) {
			protocol = YahooProtocol.YAHOO;
		} else {
			protocol = YahooProtocol.getProtocolOrDefault(protocolString, contactUsername);
		}
		String groupName = packet.getValue("65");
		String friendAddStatus = packet.getValue("66"); // TODO what is this: 40 - failed MSN, 0 success yahoo,
		String pending = packet.getValue("223"); // 1
		if (!pending.equals("1")) {
			log.warn("pending is not 1: " + pending);
		}
		YahooContact contact = new YahooContact(contactUsername, protocol);
		if ("0".equals(friendAddStatus)) {
			// log.info("Me: " + myName + " friend added: " + userId + ", pending: " + pending + ", protocol: "
			// + protocol);
			ContactGroupImpl group = new ContactGroupImpl(groupName);
			group.add(contact);
			addContact(pending, contact, group);
		} else {
			ContactAddFailure failure = ContactAddFailure.get(friendAddStatus);
			log.warn("Me: " + me + " Friend add status is not 0: " + friendAddStatus);
			if (failure == ContactAddFailure.ALREADY_IN_GROUP) {
				if (!sessionRoster.getContacts().contains(contact)) {
					log.warn("Getting already in group, but can't find it.  It will be added");
					ContactGroupImpl group = new ContactGroupImpl(groupName);
					group.add(contact);
					addContact(pending, contact, group);
					return;
				}
			}
			if (failure == null) {
				failure = ContactAddFailure.UNKNOWN;
			}
			sessionRoster.receivedContactAddFailure(contact, failure, friendAddStatus);
		}
	}

	private void addContact(String pending, YahooContact contact, ContactGroupImpl group) {
		if ("1".equals(pending)) {
			sessionStatus.addPending(contact);
		} else {
			log.warn("added contact with pending: " + pending);
		}
		// TODO do I need this
		boolean contactWasAdded = true;
		sessionRoster.receivedContactAddAck(contact);
		boolean groupWasAdded = sessionGroup.possibleAddGroup(group);
		sessionStatus.publishPending(contact);
		log.info("Contact was added: " + contactWasAdded + " along with the group: " + groupWasAdded);
	}
}
