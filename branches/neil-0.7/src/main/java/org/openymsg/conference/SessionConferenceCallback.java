package org.openymsg.conference;

import org.openymsg.Conference;
import org.openymsg.ConferenceStatus;

public interface SessionConferenceCallback {
	void conferenceStatusUpdate(String conferenceId, ConferenceStatus status);
	void invitedToConference(Conference conference, String message);
	void conferenceMessageReceived(Conference conference, String message);
}
