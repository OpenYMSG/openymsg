package org.openymsg.contact;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.ContactAddFailure;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.execute.read.SinglePacketResponse;
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
		String me = packet.getValue("1");
		String contactUsername = packet.getValue("7");
		String groupName = packet.getValue("65");
		String friendAddStatus = packet.getValue("66"); // TODO what is this: 40 - failed MSN, 0 success yahoo,
		String pending = packet.getValue("223"); // 1
		String protocol = packet.getValue("241");

		// TODO should we always check for this?
		if (packet.status != 1) {
			log.warn("Ack is not an ack: " + packet.status);
			// log.warn("Me: " + myName + " Add buddy attempt: " + primaryID + ", " + userId + " problem: "
			// + friendAddStatus + " not an ack: " + pkt.status);
		}

		YahooProtocol yahooProtocol = YahooProtocol.YAHOO;
		if (protocol != null) {
			yahooProtocol = YahooProtocol.getProtocolOrDefault(protocol, contactUsername);
		}
		YahooContact contact = new YahooContact(contactUsername, yahooProtocol);

		if ("0".equals(friendAddStatus)) {
			// log.info("Me: " + myName + " friend added: " + userId + ", pending: " + pending + ", protocol: "
			// + protocol);
			ContactGroupImpl group = new ContactGroupImpl(groupName);
			group.add(contact);
			addContact(pending, contact, group);
		} else {
			ContactAddFailure failure = ContactAddFailure.get(friendAddStatus);
			log.warn("Me: " + me + " Friend add status is not 0: " + friendAddStatus);

			if (failure == null) {
				failure = ContactAddFailure.UNKNOWN;
				sessionRoster.receivedContactAddFailure(contact, failure, friendAddStatus);
				return;
			}

			switch (failure) {
			case ALREADY_IN_GROUP:
				if (!sessionRoster.getContacts().contains(contact)) {
					log.warn("Getting already in group, but can't find it.  It will be added");
					ContactGroupImpl group = new ContactGroupImpl(groupName);
					group.add(contact);
					addContact(pending, contact, group);
				} else {
					sessionRoster.contactAddFailure(contact, failure);
				}
				break;
			case NOT_REMOTE_USER:
			case NOT_YAHOO_USER:
			case SOMETHING:
			case SOMETHING_ELSE:
				sessionRoster.contactAddFailure(contact, failure);
				break;
			default:
				failure = ContactAddFailure.UNKNOWN;
				sessionRoster.receivedContactAddFailure(contact, failure, friendAddStatus);
			}
		}

	}

	private void addContact(String pending, YahooContact contact, ContactGroupImpl group) {
		if ("1".equals(pending)) {
			sessionStatus.addPending(contact);
		} else {
			log.warn("added contact with pending: " + pending);
		}
		boolean contactWasAdded = sessionRoster.possibleAddContact(contact);
		boolean groupWasAdded = sessionGroup.possibleAddGroup(group);
		sessionStatus.publishPending(contact);
		log.info("Contact was added: " + contactWasAdded + " along with the group: " + groupWasAdded);
	}

}
