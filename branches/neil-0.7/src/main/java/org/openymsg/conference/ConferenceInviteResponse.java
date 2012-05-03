package org.openymsg.conference;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming CONFINVITE packet. We get one of these when we are being invited to join someone else's
 * conference. Note: it is possible for conference packets (ie: logon) can arrive before the invite. These are buffered
 * until the invite is received.
 * @param pkt
 */
public class ConferenceInviteResponse extends AbstractConferenceResponse {
	/** logger */
	private static final Log log = LogFactory.getLog(ConferenceInviteResponse.class);

	public ConferenceInviteResponse(SessionConferenceImpl sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		String to = packet.getValue("1");
		@SuppressWarnings("unused")
		String value13 = packet.getValue("13");
		String from = packet.getValue("50");
		final String[] invitedContactIds = packet.getValues("52");
		String conferenceId = packet.getValue("57");
		String message = packet.getValue("58");
		@SuppressWarnings("unused")
		String unicode = packet.getValue("97"); // 1
		@SuppressWarnings("unused")
		String value233 = packet.getValue("233"); // unknown
		@SuppressWarnings("unused")
		String value234 = packet.getValue("234"); // duplicate of conferenceId?

		YahooContact inviter = new YahooContact(from, YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = getContacts(invitedContactIds);

		final String[] memberContactId = packet.getValues("53");
		Set<YahooContact> memberContacts = getContacts(memberContactId);
		String otherInvitedUserIdsCommaSeparated = packet.getValue("51");
		// Set<YahooUser> otherInvitedUsers = getUsers(otherInvitedUserIdsCommaSeparated);
		// invitedUsers.addAll(otherInvitedUsers);

		YahooConference conference = new ConferenceImpl(conferenceId);
		if (invitedContactIds.length == 0 && memberContactId.length == 0) {
			log.debug("Correctly not handling empty invite: " + packet);
			return;
		}

		// Add inviter to members
		memberContacts.add(inviter);

		if (to.equals(from)) {
			sessionConference
					.receivedConferenceInviteAck(conference, inviter, invitedContacts, memberContacts, message);
		} else {
			sessionConference.receivedConferenceInvite(conference, inviter, invitedContacts, memberContacts, message);
		}

		// Add the users
		// yc.addUsers(invitedUserIds);
		// yc.addUsers(currentUserIds);
		// yc.addUser(from);
		// Fire invite event
		// if (!yc.isClosed()) // Should never be closed for invite!
		// parentSession.fire(se, ServiceType.CONFINVITE);
		// TODO - invite buffer
		// Set invited status and work through buffered conference
		// synchronized (yc) {
		// Queue<YMSG9Packet> buffer = yc.inviteReceived();
		// for (YMSG9Packet packet : buffer) {
		// process(packet);
		// }
		// }
	}

	private Set<YahooContact> getContacts(final String[] contactIds) {
		final Set<YahooContact> contacts = new HashSet<YahooContact>();
		for (final String contactId : contactIds) {
			YahooContact contact = new YahooContact(contactId, YahooProtocol.YAHOO);
			contacts.add(contact);
		}
		return contacts;
	}

}
