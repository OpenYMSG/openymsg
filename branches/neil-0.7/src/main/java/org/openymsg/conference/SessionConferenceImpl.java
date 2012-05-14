package org.openymsg.conference;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;
import org.openymsg.util.CollectionUtils;

public class SessionConferenceImpl implements SessionConference {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionConferenceImpl.class);
	private String username;
	private YahooConnection executor;
	private SessionConferenceCallback callback;
	private Map<String, YahooConference> conferences = new ConcurrentHashMap<String, YahooConference>();
	// private Map<String, YahooConferenceStatus> conferenceStatuses = new ConcurrentHashMap<String,
	// YahooConferenceStatus>();
	private Map<String, ConferenceMembershipImpl> conferenceMemberships = new ConcurrentHashMap<String, ConferenceMembershipImpl>();

	public SessionConferenceImpl(String username, YahooConnection executor, SessionConferenceCallback callback)
			throws IllegalArgumentException {
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
		this.executor.register(ServiceType.CONFLOGON, new ConferenceAcceptResponse(this));
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
	public void leaveConference(YahooConference conference) throws IllegalArgumentException {
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
	public void acceptConferenceInvite(YahooConference conference) throws IllegalArgumentException {
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
	public YahooConference createConference(String conferenceId, Set<YahooContact> contacts, String message)
			throws IllegalArgumentException {
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
	public void declineConferenceInvite(YahooConference conference, String message) throws IllegalArgumentException {
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
	public void extendConference(YahooConference conference, Set<YahooContact> contacts, String message)
			throws IllegalArgumentException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		if (contacts == null) {
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
		this.executor.execute(new ExtendConferenceMessage(username, conference, membership, contacts, message));
	}

	@Override
	public YahooConference getConference(String conferenceId) throws IllegalArgumentException {
		if (conferenceId == null) {
			throw new IllegalArgumentException("ConferenceId cannot be null");
		}
		return this.conferences.get(conferenceId);
	}

	// @Override
	// public YahooConferenceStatus getConferenceStatus(String conferenceId) {
	// return this.conferenceStatuses.get(conferenceId);
	// }
	//
	// public void conferenceStatusUpdate(String conferenceId, YahooConferenceStatus status) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	public void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message) {
		this.callback.receivedConferenceMessage(conference, contact, message);
	}

	public void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message) {
		ConferenceMembershipImpl membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			log.warn("no membership for decline: " + conference);
			membership = new ConferenceMembershipImpl();
			this.conferenceMemberships.put(conference.getId(), membership);
		}
		membership.addDecline(contact);
		this.callback.receivedConferenceDecline(conference, contact, message);
	}

	@Override
	public Set<YahooConference> getConferences() {
		return CollectionUtils.protectedSet(this.conferences.values());
	}

	public ConferenceMembership getConferenceMembership(YahooConference conference) {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
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
		// TODO add me?
		membership.addMember(members);
		membership.addInvited(invited);
		this.callback.receivedConferenceInvite(conference, inviter, invited, members, message);
	}

	public void receivedConferenceInviteAck(YahooConference conference, YahooContact inviter,
			Set<YahooContact> invited, Set<YahooContact> members, String message) {
		ConferenceMembershipImpl membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			membership = new ConferenceMembershipImpl();
			this.conferenceMemberships.put(conference.getId(), membership);
		} else {
			log.debug("invited to previously added conference: " + conference);
			// TODO reset membership?
		}
		membership.addMember(members);
		membership.addInvited(invited);
	}

	public void receivedConferenceAccept(YahooConference conference, YahooContact contact) {
		ConferenceMembershipImpl membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			log.warn("no membership for accept: " + conference);
			membership = new ConferenceMembershipImpl();
			this.conferenceMemberships.put(conference.getId(), membership);
		}
		membership.addMember(contact);
		callback.receivedConferenceAccept(conference, contact);
	}

	public void receivedConferenceExtend(YahooConference conference, YahooContact inviter, Set<YahooContact> invited) {
		ConferenceMembershipImpl membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			log.warn("no membership for accept: " + conference);
			membership = new ConferenceMembershipImpl();
			this.conferenceMemberships.put(conference.getId(), membership);
		}
		membership.addInvited(invited);
		callback.receivedConferenceExtend(conference, inviter, invited);
	}

	public void receivedConferenceLeft(YahooConference conference, YahooContact contact) {
		ConferenceMembershipImpl membership = this.conferenceMemberships.get(conference.getId());
		if (membership == null) {
			log.warn("no membership for accept: " + conference);
			membership = new ConferenceMembershipImpl();
			this.conferenceMemberships.put(conference.getId(), membership);
		}
		membership.addLeft(contact);
		callback.receivedConferenceLeft(conference, contact);
	}
}
