package org.openymsg.conference;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooConference;
import org.openymsg.YahooConferenceStatus;
import org.openymsg.YahooContact;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;
import org.openymsg.util.CollectionUtils;

public class SessionConferenceImpl implements SessionConference {
	private static final Log log = LogFactory.getLog(SessionConferenceImpl.class);
	private String username;
	private Executor executor;
	private SessionConferenceCallback callback;
	private Map<String, YahooConference> conferences = new ConcurrentHashMap<String, YahooConference>();
	private Map<String, YahooConferenceStatus> conferenceStatuses = new ConcurrentHashMap<String, YahooConferenceStatus>();
	private Map<String, ConferenceMembershipImpl> conferenceMemberships = new ConcurrentHashMap<String, ConferenceMembershipImpl>();

	public SessionConferenceImpl(String username, Executor executor, SessionConferenceCallback callback) {
		if (username == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}
		if (executor == null) {
			throw new IllegalArgumentException("Executor cannot be null");
		}
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		this.username = username;
		this.executor = executor;
		this.callback = callback;
		this.executor.register(ServiceType.CONFMSG, new ConferenceMessageResponse(this));
		this.executor.register(ServiceType.CONFINVITE, new ConferenceInviteResponse(this));
		this.executor.register(ServiceType.CONFDECLINE, new ConferenceDeclineResponse(this));
	}

	@Override
	public void sendConferenceMessage(YahooConference conference, String message) throws IllegalStateException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		this.executor.execute(new SendConfereneMessage(username, conference, membership, message));
	}

	@Override
	public void leaveConference(YahooConference conference) throws IllegalStateException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		this.executor.execute(new LeaveConferenceMessage(username, conference, membership));
	}

	@Override
	public void acceptConferenceInvite(YahooConference conference) throws IllegalStateException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		this.executor.execute(new AcceptConferenceMessage(username, conference, membership));
	}

	// TODO generate conferenceId
	@Override
	public YahooConference createConference(String conferenceId, Set<YahooContact> contacts, String message) {
		if (conferenceId == null || conferenceId.trim().isEmpty()) {
			throw new IllegalArgumentException("ConferenceId cannot be null");
		}
		if (contacts == null) {
			throw new IllegalArgumentException("Contact cannot be null, but can be empty");
		}
		// TODO - how to add invited ids to conference
		YahooConference conference = new ConferenceImpl(conferenceId);
		// TODO - cannot reused id
		this.conferenceMemberships.put(conferenceId, new ConferenceMembershipImpl());
		this.executor.execute(new CreateConferenceMessage(username, conference, contacts, message));
		return conference;
	}

	@Override
	public void declineConferenceInvite(YahooConference conference, String message) throws IllegalStateException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		this.executor.execute(new DeclineConferenceMessage(username, conference, membership, message));
	}

	@Override
	public void extendConference(YahooConference conference, YahooContact contact, String message)
			throws IllegalStateException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}

		// final String id = username;
		// if (primaryID.getId().equals(id) || loginID.getId().equals(id) || identities.containsKey(id)) {
		// throw new IllegalIdentityException(id + " is an identity of this session and cannot be used here");
		// }
		ConferenceMembership membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		this.executor.execute(new ExtendConferenceMessage(username, conference, membership, contact, message));
	}

	@Override
	public YahooConference getConference(String conferenceId) {
		return this.conferences.get(conferenceId);
	}

	@Override
	public YahooConferenceStatus getConferenceStatus(String conferenceId) {
		return this.conferenceStatuses.get(conferenceId);
	}

	public void conferenceStatusUpdate(String conferenceId, YahooConferenceStatus status) {
		// TODO Auto-generated method stub

	}

	public void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message) {
		// TODO Auto-generated method stub

	}

	public void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<YahooConference> getConferences() {
		return CollectionUtils.protectedSet(this.conferences.values());
	}

	public ConferenceMembership getConferenceMembership(YahooConference conference) {
		return this.conferenceMemberships.get(conference.getId());
	}

	public void receivedConferenceInvite(YahooConference conference, YahooContact inviter, Set<YahooContact> invited,
			Set<YahooContact> members, String message) {
		ConferenceMembershipImpl membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			membership = new ConferenceMembershipImpl();
			this.conferenceMemberships.put(conference.getId(), membership);
		} else {
			log.debug("invited to previously added conference: " + conference);
			// TODO reset membership?
		}
		// TODO add inviter?
		// TODO add me?
		membership.addMember(members);
		membership.addInvited(invited);
		this.callback.receivedConferenceInvite(conference, inviter, invited, members, message);
	}
}
