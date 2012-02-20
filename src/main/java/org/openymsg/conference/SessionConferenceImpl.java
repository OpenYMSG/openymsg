package org.openymsg.conference;

import org.openymsg.Conference;
import org.openymsg.execute.Executor;

public class SessionConferenceImpl implements SessionConference {
	private String username;
	private Executor executor;

	public SessionConferenceImpl(String username, Executor executor) {
		this.username = username;
		this.executor = executor;
	}

	@Override
	public void sendConferenceMessage(Conference conference, String message) throws IllegalStateException {
		// TODO - check status
		// checkStatus();
		// TODO - check for existing room
		this.executor.execute(new SendConfereneMessage(username, conference, message));
	}

	@Override
	public void leaveConference(Conference conference) throws IllegalStateException {
		// checkStatus();
		// TODO - check for existing room
		this.executor.execute(new LeaveConferenceMessage(username, conference));
	}

	@Override
	public void acceptConferenceInvite(Conference conference) throws IllegalStateException {
//		checkStatus();
		this.executor.execute(new AcceptConferenceMessage(username, conference));
	}

}
