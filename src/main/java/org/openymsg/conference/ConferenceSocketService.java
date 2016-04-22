package org.openymsg.conference;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooConference;
import org.openymsg.YahooConferenceStatus;
import org.openymsg.YahooContact;

/**
 * Handle any conference processing before passing on callback calls
 * 
 * @author nhart
 *
 */
public class ConferenceSocketService implements SessionConferenceCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(ConferenceSocketService.class);

	private final SessionConferenceCallback callback;
	private final ConferenceServiceState state;

	public ConferenceSocketService(SessionConferenceCallback callback, ConferenceServiceState state) {
		this.callback = callback;
		this.state = state;
	}

	@Override
	public void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message) {
		callback.receivedConferenceMessage(conference, contact, message);
	}

	@Override
	public void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message) {
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		if (membership == null) {
			log.warn("no membership for decline: " + conference);
			membership = new ConferenceMembershipImpl();
			state.addMembership(conference.getId(), membership);
		}
		membership.addDecline(contact);
		callback.receivedConferenceDecline(conference, contact, message);
	}

	@Override
	public void receivedConferenceInvite(YahooConference conference, YahooContact inviter, Set<YahooContact> invited,
			Set<YahooContact> members, String message) {
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		if (membership == null) {
			membership = new ConferenceMembershipImpl();
			state.addMembership(conference.getId(), membership);
		} else {
			log.debug("invited to previously added conference: " + conference);
			// TODO reset membership?
		}
		// TODO add me?
		membership.addMember(members);
		membership.addInvited(invited);
		this.callback.receivedConferenceInvite(conference, inviter, invited, members, message);
	}

	@Override
	public void receivedConferenceInviteAck(YahooConference conference, Set<YahooContact> invited,
			Set<YahooContact> members, String message) {
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		if (membership == null) {
			log.warn("getting ack for a conference we don't have");
			membership = new ConferenceMembershipImpl();
			state.addMembership(conference.getId(), membership);
		} else {
			log.debug("Got ack for conference: " + conference);
		}
		membership.addMember(members);
		membership.addInvited(invited);
		callback.receivedConferenceInviteAck(conference, invited, members, message);
	}

	@Override
	public void receivedConferenceAccept(YahooConference conference, YahooContact contact) {
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		if (membership == null) {
			log.warn("no membership for accept: " + conference);
			membership = new ConferenceMembershipImpl();
			state.addMembership(conference.getId(), membership);
		}
		membership.addMember(contact);
		callback.receivedConferenceAccept(conference, contact);
	}

	@Override
	public void receivedConferenceExtend(YahooConference conference, YahooContact inviter, Set<YahooContact> invited) {
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		if (membership == null) {
			log.warn("no membership for accept: " + conference);
			membership = new ConferenceMembershipImpl();
			state.addMembership(conference.getId(), membership);
		}
		membership.addInvited(invited);
		callback.receivedConferenceExtend(conference, inviter, invited);
	}

	@Override
	public void receivedConferenceLeft(YahooConference conference, YahooContact contact) {
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		if (membership == null) {
			log.warn("no membership for accept: " + conference);
			membership = new ConferenceMembershipImpl();
			state.addMembership(conference.getId(), membership);
		}
		membership.addLeft(contact);
		callback.receivedConferenceLeft(conference, contact);
	}

	@Override
	public void conferenceStatusUpdate(String conferenceId, YahooConferenceStatus status) {
		callback.conferenceStatusUpdate(conferenceId, status);
	}
}
