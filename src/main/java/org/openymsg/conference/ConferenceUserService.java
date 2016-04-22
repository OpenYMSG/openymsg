package org.openymsg.conference;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.conference.message.AcceptConferenceMessage;
import org.openymsg.conference.message.CreateConferenceMessage;
import org.openymsg.conference.message.DeclineConferenceMessage;
import org.openymsg.conference.message.ExtendConferenceMessage;
import org.openymsg.conference.message.LeaveConferenceMessage;
import org.openymsg.conference.message.SendConfereneMessage;
import org.openymsg.connection.YahooConnection;

public class ConferenceUserService implements SessionConference {
	/** logger */
	private static final Log log = LogFactory.getLog(ConferenceUserService.class);
	private final String username;
	private final YahooConnection connection;
	private final ConferenceServiceState state;

	public ConferenceUserService(String username, YahooConnection connection, ConferenceServiceState state)
			throws IllegalArgumentException {
		if (username == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		if (state == null) {
			throw new IllegalArgumentException("State cannot be null");
		}
		this.username = username;
		this.connection = connection;
		this.state = state;
	}

	// protected ConferenceSocketService
	// createCallbackHandler(SessionConferenceCallback callback) {
	// return new ConferenceSocketService(callback, conferenceMemberships);
	// }

	@Override
	public void sendConferenceMessage(YahooConference conference, String message) throws IllegalStateException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		ConferenceMembership membership = state.getMembership(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		connection.execute(new SendConfereneMessage(username, conference, membership, message));
	}

	@Override
	public void leaveConference(YahooConference conference) throws IllegalArgumentException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		ConferenceMembership membership = state.getMembership(conference.getId());
		if (membership == null) {
			if (state.hasConference(conference.getId())) {
				log.error("Trying to leave with a conference in conferences, but not in membership: "
						+ conference.getId());
				return;
			} else {
				throw new IllegalArgumentException("Unknown conference: " + conference);
			}
		}
		connection.execute(new LeaveConferenceMessage(username, conference, membership));
	}

	@Override
	public void acceptConferenceInvite(YahooConference conference) throws IllegalArgumentException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		ConferenceMembership membership = state.getMembership(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		connection.execute(new AcceptConferenceMessage(username, conference, membership));
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
		YahooConference conference = new YahooConference(conferenceId);
		// TODO - cannot reused id
		state.addMembership(conferenceId, new ConferenceMembershipImpl());
		connection.execute(new CreateConferenceMessage(username, conference, contacts, message));
		return conference;
	}

	@Override
	public void declineConferenceInvite(YahooConference conference, String message) throws IllegalArgumentException {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		ConferenceMembership membership = state.getMembership(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		connection.execute(new DeclineConferenceMessage(username, conference, membership, message));
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
		// if (primaryID.getId().equals(id) || loginID.getId().equals(id) ||
		// identities.containsKey(id)) {
		// throw new IllegalIdentityException(id + " is an identity of this
		// session and cannot be used here");
		// }
		ConferenceMembership membership = state.getMembership(conference.getId());
		if (membership == null) {
			throw new IllegalArgumentException("Unknown conference: " + conference);
		}
		connection.execute(new ExtendConferenceMessage(username, conference, membership, contacts, message));
	}

	@Override
	public YahooConference getConference(String conferenceId) throws IllegalArgumentException {
		if (conferenceId == null) {
			throw new IllegalArgumentException("ConferenceId cannot be null");
		}
		if (!state.hasConference(conferenceId)) {
			log.warn("Conference not found for: " + conferenceId);
		}
		return state.getConference(conferenceId);
	}

	@Override
	public Set<YahooConference> getConferences() {
		return state.getConferences();
	}

	public ConferenceMembership getConferenceMembership(YahooConference conference) {
		if (conference == null) {
			throw new IllegalArgumentException("Conference cannot be null");
		}
		return state.getMembership(conference.getId());
	}

}
