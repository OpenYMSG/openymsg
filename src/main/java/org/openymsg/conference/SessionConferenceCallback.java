package org.openymsg.conference;

import java.util.Set;

import org.openymsg.YahooConference;
import org.openymsg.YahooConferenceStatus;
import org.openymsg.YahooContact;

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
	 * Session was invited to a confernce
	 * @param conference conference
	 * @param message message from the invite, may be null
	 */
	void invitedToConference(YahooConference conference, YahooContact contact, String message);

	/**
	 * Message received from the conference
	 * @param conference conference with the message
	 * @param message message
	 */
	void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message);

	void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message);

	void receivedConferenceInvite(YahooConference conference, YahooContact inviter, Set<YahooContact> invited,
			Set<YahooContact> members, String message);
}
