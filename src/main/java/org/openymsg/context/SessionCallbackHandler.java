package org.openymsg.context;

import java.util.Set;

import org.openymsg.Name;
import org.openymsg.SessionImpl;
import org.openymsg.YahooConference;
import org.openymsg.YahooConferenceStatus;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooContactStatus;
import org.openymsg.YahooSessionCallback;
import org.openymsg.contact.roster.ContactAddFailure;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.context.session.LogoutReason;

public class SessionCallbackHandler implements YahooSessionCallback {
	private SessionImpl session;
	private YahooSessionCallback callback;

	public SessionCallbackHandler(SessionImpl session, YahooSessionCallback callback) {
		this.session = session;
		this.callback = callback;
	}

	@Override
	public void receivedMessage(YahooContact from, String message) {
		callback.receivedMessage(from, message);
	}

	@Override
	public void receivedBuzz(YahooContact from) {
		callback.receivedBuzz(from);
	}

	@Override
	public void receivedOfflineMessage(YahooContact from, String message, long timestampInMillis) {
		callback.receivedOfflineMessage(from, message, timestampInMillis);
	}

	@Override
	public void receivedTypingNotification(YahooContact from, boolean isTyping) {
		callback.receivedTypingNotification(from, isTyping);
	}

	@Override
	public void connectionSuccessful() {
		session.connectionSuccessful();
		callback.connectionSuccessful();
	}

	@Override
	public void connectionFailure() {
		session.connectionFailed();
		callback.connectionFailure();
	}

	@Override
	public void connectionPrematurelyEnded() {
		session.connectionPrematurelyEnded();
		callback.connectionPrematurelyEnded();
	}

	@Override
	public void authenticationSuccess() {
		session.authenticationSuccess();
		callback.authenticationSuccess();
	}

	@Override
	public void authenticationFailure(AuthenticationFailure failure) {
		session.failedAuthentication();
		callback.authenticationFailure(failure);
		// TODO shutdown
	}

	@Override
	public void logoffNormalComplete() {
		session.loggedOfNormally();
		callback.logoffNormalComplete();
	}

	@Override
	public void logoffForced(LogoutReason state) {
		session.loggedOffForced();
		callback.logoffForced(state);
	}

	@Override
	public void rosterLoaded() {
		callback.rosterLoaded();
	}

	@Override
	public void addedContact(YahooContact contact) {
		callback.addedContact(contact);
	}

	@Override
	public void removedContact(YahooContact contact) {
		callback.removedContact(contact);
	}

	@Override
	public void receivedContactAddFailure(YahooContact contact, ContactAddFailure failure,
			String additionalInformation) {
		callback.receivedContactAddFailure(contact, failure, additionalInformation);
	}

	@Override
	public void receivedContactAddAccepted(YahooContact contact) {
		callback.receivedContactAddAccepted(contact);
	}

	@Override
	public void receivedContactAddDeclined(YahooContact contact, String message) {
		callback.receivedContactAddDeclined(contact, message);
	}

	@Override
	public void receivedContactAddRequest(String id, YahooContact contact, Name name, String message) {
		callback.receivedContactAddRequest(id, contact, name, message);
	}

	@Override
	public void addedGroups(Set<YahooContactGroup> contactGroups) {
		callback.addedGroups(contactGroups);
	}

	@Override
	public void statusUpdate(YahooContact contact, YahooContactStatus status) {
		callback.statusUpdate(contact, status);
	}

	@Override
	public void conferenceStatusUpdate(String conferenceId, YahooConferenceStatus status) {
		callback.conferenceStatusUpdate(conferenceId, status);
	}

	@Override
	public void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message) {
		callback.receivedConferenceMessage(conference, contact, message);
	}

	@Override
	public void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message) {
		callback.receivedConferenceDecline(conference, contact, message);
	}

	@Override
	public void receivedConferenceInvite(YahooConference conference, YahooContact inviter, Set<YahooContact> invited,
			Set<YahooContact> members, String message) {
		callback.receivedConferenceInvite(conference, inviter, invited, members, message);
	}

	@Override
	public void receivedConferenceAccept(YahooConference conference, YahooContact contact) {
		callback.receivedConferenceAccept(conference, contact);
	}

	@Override
	public void receivedConferenceExtend(YahooConference conference, YahooContact inviter, Set<YahooContact> invited) {
		callback.receivedConferenceExtend(conference, inviter, invited);
	}

	@Override
	public void receivedConferenceLeft(YahooConference conference, YahooContact contact) {
		callback.receivedConferenceLeft(conference, contact);
	}

	@Override
	public void receivedConferenceInviteAck(YahooConference conference, Set<YahooContact> invited,
			Set<YahooContact> members, String message) {
		callback.receivedConferenceInviteAck(conference, invited, members, message);
	}

}
