package org.openymsg.conference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openymsg.Conference;
import org.openymsg.ConferenceStatus;
import org.openymsg.Contact;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;
import org.openymsg.util.CollectionUtils;

public class SessionConferenceImpl implements SessionConference, SessionConferenceCallback {
	private String username;
	private Executor executor;
	private Map<String, Conference> conferences = new HashMap<String, Conference>();
	private Map<String, ConferenceStatus> conferenceStatuses = new HashMap<String, ConferenceStatus>();
	private Map<String, ConferenceMembership> conferenceMemberships = new HashMap<String, ConferenceMembership>();

	public SessionConferenceImpl(String username, Executor executor) {
		this.username = username;
		this.executor = executor;
		this.executor.register(ServiceType.CONFMSG, new ConferenceMessageResponse(this));
		this.executor.register(ServiceType.CONFINVITE, new ConferenceInviteResponse(this));
		this.executor.register(ServiceType.CONFDECLINE, new ConferenceDeclineResponse(this));
	}

	@Override
	public void sendConferenceMessage(Conference conference, String message) throws IllegalStateException {
		// TODO - check status
		// checkStatus();
		// TODO - check for existing room
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		this.executor.execute(new SendConfereneMessage(username, conference, membership, message));
	}

	@Override
	public void leaveConference(Conference conference) throws IllegalStateException {
		// checkStatus();
		// TODO - check for existing room
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		this.executor.execute(new LeaveConferenceMessage(username, conference, membership));
	}

	@Override
	public void acceptConferenceInvite(Conference conference) throws IllegalStateException {
		// checkStatus();
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		this.executor.execute(new AcceptConferenceMessage(username, conference, membership));
	}

	@Override
	public Conference createConference(String conferenceId, Set<Contact> contacts, String message) {
		// TODO - how do you invite MSN users?
		// checkStatus();
		// TODO - how to add invited ids to conference
		Conference conference = new ConferenceImpl(conferenceId);
		this.executor.execute(new CreateConferenceMessage(username, conference, contacts, message));
		return conference;
	}

	@Override
	public void declineConferenceInvite(Conference conference, String message) throws IllegalStateException {
		// checkStatus();
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		this.executor.execute(new DeclineConferenceMessage(username, conference, membership, message));
	}

	@Override
	public void extendConference(Conference conference, Contact contact, String message) throws IllegalStateException {
		// checkStatus();

		// final String id = username;
		// if (primaryID.getId().equals(id) || loginID.getId().equals(id) || identities.containsKey(id)) {
		// throw new IllegalIdentityException(id + " is an identity of this session and cannot be used here");
		// }
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		this.executor.execute(new ExtendConferenceMessage(username, conference, membership, contact, message));
	}

	@Override
	public Conference getConference(String conferenceId) {
		return this.conferences.get(conferenceId);
	}

	@Override
	public ConferenceStatus getConferenceStatus(String conferenceId) {
		return this.conferenceStatuses.get(conferenceId);
	}

	@Override
	public void conferenceStatusUpdate(String conferenceId, ConferenceStatus status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void invitedToConference(Conference conference, Contact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void conferenceMessageReceived(Conference conference, Contact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void conferenceDeclineReceived(Conference conference, Contact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Conference> getConferences() {
		return CollectionUtils.protectedSet(this.conferences.values());
	}

}
