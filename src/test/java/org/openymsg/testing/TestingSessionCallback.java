package org.openymsg.testing;

import java.util.Set;

import org.openymsg.Name;
import org.openymsg.YahooConference;
import org.openymsg.YahooConferenceStatus;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooContactStatus;
import org.openymsg.YahooSessionCallback;
import org.openymsg.contact.roster.ContactAddFailure;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.context.session.LogoutReason;

public class TestingSessionCallback implements YahooSessionCallback {

	private AuthenticationFailure failure;

	@Override
	public void receivedMessage(YahooContact from, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedBuzz(YahooContact from) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedOfflineMessage(YahooContact from, String message, long timestampInMillis) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedTypingNotification(YahooContact from, boolean isTyping) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionSuccessful() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionPrematurelyEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void authenticationSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void authenticationFailure(AuthenticationFailure failure) {
		this.failure = failure;
	}

	@Override
	public void logoffNormalComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logoffForced(LogoutReason reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rosterLoaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedContact(YahooContact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removedContact(YahooContact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedContactAddFailure(YahooContact contact, ContactAddFailure failure, String additionalInformation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedContactAddAccepted(YahooContact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedContactAddDeclined(YahooContact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedContactAddRequest(YahooContact contact, Name name, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedGroups(Set<YahooContactGroup> contactGroups) {
		// TODO Auto-generated method stub

	}

	@Override
	public void statusUpdate(YahooContact contact, YahooContactStatus status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void conferenceStatusUpdate(String conferenceId, YahooConferenceStatus status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedConferenceMessage(YahooConference conference, YahooContact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedConferenceDecline(YahooConference conference, YahooContact contact, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedConferenceInvite(YahooConference conference, YahooContact inviter, Set<YahooContact> invited,
			Set<YahooContact> members, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedConferenceExtend(YahooConference conference, YahooContact inviter, Set<YahooContact> invited) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedConferenceAccept(YahooConference conference, YahooContact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedConferenceLeft(YahooConference conference, YahooContact contact) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the failure
	 */
	public AuthenticationFailure getFailure() {
		return failure;
	}

}
