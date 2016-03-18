package org.openymsg.conference;

import org.openymsg.YahooConference;
import org.openymsg.YahooConferenceStatus;
import org.openymsg.YahooContact;

import java.util.Set;

/**
 * Callback for Conference notifications.
 * @author neilhart
 */
public interface SessionConferenceCallback {
	/**
	 * Changes to the conference status
	 * @param conferenceId conference wit the change
	 * @param status new conference status
	 */
	void conferenceStatusUpdate(String conferenceId, YahooConferenceStatus status);

	/**
	 * Message received from the conference
	 * @param conference conference with the message
	 * @param message message
	 */
	void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message);

	void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message);

	void receivedConferenceInvite(YahooConference conference, YahooContact inviter, Set<YahooContact> invited,
			Set<YahooContact> members, String message);

	void receivedConferenceExtend(YahooConference conference, YahooContact inviter, Set<YahooContact> invited);

	void receivedConferenceAccept(YahooConference conference, YahooContact contact);

	void receivedConferenceLeft(YahooConference conference, YahooContact contact);

	void receivedConferenceInviteAck(YahooConference conference, Set<YahooContact> invited, Set<YahooContact> members,
			String message);
}
