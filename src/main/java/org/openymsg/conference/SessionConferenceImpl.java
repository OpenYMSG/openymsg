package org.openymsg.conference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openymsg.Conference;
import org.openymsg.ConferenceStatus;
import org.openymsg.execute.Executor;

public class SessionConferenceImpl implements SessionConference {
	private String username;
	private Executor executor;
	private Map<String, ConferenceImpl> conferences = new HashMap<String, ConferenceImpl>();
	private Map<String, ConferenceStatus> conferenceStatuses = new HashMap<String, ConferenceStatus>();

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
		// checkStatus();
		this.executor.execute(new AcceptConferenceMessage(username, conference));
	}

	@Override
	public Conference createConference(String conferenceId, Set<String> invitedIds, String message) {
		// TODO - how do you invite MSN users?
		// checkStatus();
		// TODO - how to add invited ids to conference
		Conference conference = new ConferenceImpl(conferenceId);
		this.executor.execute(new CreateConferenceMessage(username, conference, invitedIds, message));
		return conference;
	}

	@Override
	public void declineConferenceInvite(Conference conference, String message) throws IllegalStateException {
		// checkStatus();
		this.executor.execute(new DeclineConferenceMessage(username, conference, message));
	}

	@Override
	public void extendConference(Conference conference, String invitedId, String message) throws IllegalStateException {
//		checkStatus();

//		final String id = username;
//		if (primaryID.getId().equals(id) || loginID.getId().equals(id) || identities.containsKey(id)) {
//			throw new IllegalIdentityException(id + " is an identity of this session and cannot be used here");
//		}
		this.executor.execute(new ExtendConferenceMessage(username, conference, invitedId, message));
	}

	@Override
	public Conference getConference(String conferenceId) {
		return this.conferences.get(conferenceId);
	}

	@Override
	public ConferenceStatus getConferenceStatus(String conferenceId) {
		return this.conferenceStatuses.get(conferenceId);
	}

}
