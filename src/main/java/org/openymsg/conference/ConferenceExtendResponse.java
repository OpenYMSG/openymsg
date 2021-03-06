package org.openymsg.conference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Process an incoming CONFADDINVITE packet. We get one of these when we are being invited to join someone else's
 * conference. Note: it is possible for conference packets (ie: logon) can arrive before the invite. These are buffered
 * until the invite is received.
 * @param pkt
 */
public class ConferenceExtendResponse extends AbstractConferenceResponse {
	/** logger */
	private static final Log log = LogFactory.getLog(ConferenceExtendResponse.class);

	public ConferenceExtendResponse(SessionConferenceImpl sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		@SuppressWarnings("unused")
		String to = packet.getValue("1");
		// String value13 = packet.getValue("13");
		String from = packet.getValue("50");
		// final String[] invitedContactIds = packet.getValues("52");
		final String[] otherInvitedUserIds = getCommaSeperated(packet.getValues("51"));
		String conferenceId = packet.getValue("57");
		YahooContact inviter = new YahooContact(from, YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = getContacts(otherInvitedUserIds);
		// Set<YahooContact> invitedContacts = getContacts(invitedContactIds);
		// final String[] memberContactId = packet.getValues("53");
		// Set<YahooContact> memberContacts = getContacts(memberContactId);
		// invitedUsers.addAll(otherInvitedUsers);
		YahooConference conference = new YahooConference(conferenceId);
		sessionConference.receivedConferenceExtend(conference, inviter, invitedContacts);
		// if (to.equals(from)) {
		// sessionConference
		// .receivedConferenceInviteAck(conference, inviter, invitedContacts, memberContacts, message);
		// } else {
		// }
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

	private String[] getCommaSeperated(String[] values) {
		List<String> allValues = new ArrayList<String>();
		for (String value : values) {
			allValues.addAll(Arrays.asList(value.split(",")));
		}
		return allValues.toArray(new String[allValues.size()]);
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
