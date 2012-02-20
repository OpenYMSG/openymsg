package org.openymsg.conference;

import org.openymsg.Conference;

public interface SessionConference {
	void sendConferenceMessage(Conference conference, String message) throws IllegalStateException;

	void leaveConference(Conference conference) throws IllegalStateException;

	void acceptConferenceInvite(Conference conference) throws IllegalStateException;

}
