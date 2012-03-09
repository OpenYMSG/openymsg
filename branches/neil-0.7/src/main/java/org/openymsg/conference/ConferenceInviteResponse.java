package org.openymsg.conference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Conference;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming CONFINVITE packet. We get one of these when we are being invited to join someone else's
 * conference. Note: it is possible for conference packets (ie: logon) can arrive before the invite. These are buffered
 * until the invite is received.
 * @param pkt
 */
public class ConferenceInviteResponse extends AbstractConferenceResponse {
	private static final Log log = LogFactory.getLog(ConferenceInviteResponse.class);

	public ConferenceInviteResponse(SessionConferenceCallback sessionConference) {
		super(sessionConference);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		Conference conference = this.getConference(packet);
		String name = packet.getValue("58");

		final String[] invitedUserIds = packet.getValues("52");
		final String[] currentUserIds = packet.getValues("53");
		if (invitedUserIds.length == 0 && currentUserIds.length == 0) {
			log.debug("Correctly not handling empty invite: " + packet);
			return;
		}
		String otherInvitedUserIdsCommaSeparated = packet.getValue("51");
		String to = packet.getValue("1");
		String from = packet.getValue("50");
		String message = packet.getValue("58");

		// Set<YahooUser> invitedUsers = getUsers(invitedUserIds);
		// Set<YahooUser> currentUsers = getUsers(currentUserIds);
		// Set<YahooUser> otherInvitedUsers = getUsers(otherInvitedUserIdsCommaSeparated);
		// invitedUsers.addAll(otherInvitedUsers);

		// Create event
		// SessionConferenceInviteEvent se = new SessionConferenceInviteEvent(this, to, from, message, yc, invitedUsers,
		// currentUsers);
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

}
