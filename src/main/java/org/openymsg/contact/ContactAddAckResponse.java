package org.openymsg.contact;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Contact;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.group.ContactGroupImpl;
import org.openymsg.group.SessionGroupImpl;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.status.SessionStatusImpl;

/**
 * Process an incoming FRIENDADD packet. We get one of these after we've sent a FRIENDADD packet, as confirmation. It
 * contains basic details of our new friend, although it seems a bit redundant as Yahoo sents a CONTACTNEW with these
 * details before this packet.
 */
public class ContactAddAckResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(ContactAddAckResponse.class);
	private SessionContactImpl sessionContact;
	private SessionGroupImpl sessionGroup;
	private SessionStatusImpl sessionStatus;

	@Override
	public void execute(YMSG9Packet packet) {
		String username = packet.getValue("1");
		String contactUsername = packet.getValue("7");
		String groupName = packet.getValue("65");
		String friendAddStatus = packet.getValue("66"); // 40 - failed MSN, 0 success yahoo, TODO
		String pending = packet.getValue("223"); // 1
		String protocol = packet.getValue("241");

		// TODO should we always check for this?
		if (packet.status != 1) {
			log.warn("Ack is not an ack: " + packet.status);
			// log.warn("Me: " + myName + " Add buddy attempt: " + primaryID + ", " + userId + " problem: "
			// + friendAddStatus + " not an ack: " + pkt.status);
		}

		YahooProtocol yahooProtocol = YahooProtocol.getProtocolOrDefault(protocol, contactUsername);
		Contact contact = new Contact(contactUsername, yahooProtocol);
		ContactGroupImpl group = new ContactGroupImpl(groupName);
		group.add(contact);

		if ("0".equals(friendAddStatus)) {
			// log.info("Me: " + myName + " friend added: " + userId + ", pending: " + pending + ", protocol: "
			// + protocol);
			addContact(pending, contact, group);
		}
		else {
			ContactAddFailure failure = ContactAddFailure.get(friendAddStatus);
			log.warn("Me: " + username + " Friend add status is not 0: " + friendAddStatus);

			if (failure == null) {
				failure = ContactAddFailure.UNKNOWN;
				sessionContact.contactAddFailure(contact, failure, friendAddStatus);
				return;
			}

			switch (failure) {
			case ALREADY_IN_GROUP:
				if (!sessionContact.getContacts().contains(contact)) {
					log.warn("Getting already in group, but can't find it.  It will be added");
					addContact(pending, contact, group);
				}
				else {
					sessionContact.contactAddFailure(contact, failure);
				}
				break;
			case NOT_REMOTE_USER:
			case NOT_YAHOO_USER:
			case SOMETHING:
			case SOMETHING_ELSE:
				sessionContact.contactAddFailure(contact, failure);
				break;
			default:
				failure = ContactAddFailure.UNKNOWN;
				sessionContact.contactAddFailure(contact, failure, friendAddStatus);
			}
		}

	}

	private void addContact(String pending, Contact contact, ContactGroupImpl group) {
		if ("1".equals(pending)) {
			sessionStatus.addPending(contact);
		}
		else {
			log.warn("added contact with pending: " + pending);
		}
		boolean contactWasAdded = sessionContact.possibleContact(contact);
		boolean groupWasAdded = sessionGroup.possibleAddGroup(group);
		sessionStatus.publishPending(contact);
	}

}
