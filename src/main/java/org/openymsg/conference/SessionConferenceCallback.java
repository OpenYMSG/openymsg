package org.openymsg.conference;

import org.openymsg.Conference;
import org.openymsg.ConferenceStatus;

/**
 * Callback for Conference notifications.
 * 
 * @author neilhart
 */
public interface SessionConferenceCallback {
	/**
	 * Changes to the conference status
	 * 
	 * @param conferenceId conference wit the change
	 * @param status new conference status
	 */
	void conferenceStatusUpdate(String conferenceId, ConferenceStatus status);

	/**
	 * Session was invited to a confernce
	 * 
	 * @param conference conference
	 * @param message message from the invite, may be null
	 */
	void invitedToConference(Conference conference, String message);

	/**
	 * Message received from the conference
	 * 
	 * @param conference conference with the message
	 * @param message message
	 */
	void conferenceMessageReceived(Conference conference, String message);
}
