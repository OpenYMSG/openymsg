package org.openymsg;

import java.io.IOException;
import java.util.Set;

import org.openymsg.auth.SessionAuthorize;
import org.openymsg.auth.SessionAuthorizeImpl;
import org.openymsg.conference.SessionConference;
import org.openymsg.conference.SessionConferenceImpl;
import org.openymsg.contact.SessionContact;
import org.openymsg.contact.SessionContactImpl;
import org.openymsg.execute.dispatch.ExecutorImpl;
import org.openymsg.keepalive.SessionKeepAlive;
import org.openymsg.keepalive.SessionKeepAliveImpl;
import org.openymsg.mail.SessionMail;
import org.openymsg.mail.SessionMailImpl;
import org.openymsg.message.SessionMessage;
import org.openymsg.message.SessionMessageImpl;
import org.openymsg.session.SessionSession;
import org.openymsg.session.SessionSessionImpl;
import org.openymsg.status.SessionStatus;
import org.openymsg.status.SessionStatusImpl;
import org.openymsg.unknown.SessionUnknown;

public class SessionImpl implements Session {
	private SessionConfig config;
	private SessionCallback callback;
	private SessionSession session;
	private SessionMessage message;
	private SessionAuthorize authorize;
	private SessionContact contact;
	private SessionStatus status;
	private SessionConference conference;
	private SessionKeepAlive keepAlive;
	private SessionUnknown unknown;
	private SessionMail mail;

	public SessionImpl(SessionConfig config, SessionCallback callback) {
		this.config = config;
		this.callback = callback;
	}

	@Override
	public void login(String username, String password) {
		this.authorize.login(username, password);
	}

	private void initialize(String username) {
		ExecutorImpl executor = new ExecutorImpl(username);
		executor.initialize(config);
		this.authorize = new SessionAuthorizeImpl(config, executor);
		this.session = new SessionSessionImpl(username, executor);
		this.contact = new SessionContactImpl(executor);
		this.status = new SessionStatusImpl(executor);
		this.message = new SessionMessageImpl(executor, username, this.callback);
		this.conference = new SessionConferenceImpl(username, executor);
		this.mail = new SessionMailImpl(executor);
		this.unknown = new SessionUnknown(executor);
		this.keepAlive = new SessionKeepAliveImpl(executor, username);
	}

	@Override
	public void logout() {
		this.session.logout();
	}

	@Override
	public void sendMessage(Contact contact, String message) throws IllegalStateException {
		this.message.sendMessage(contact, message);
	}

	@Override
	public void sendBuzz(Contact to) throws IllegalStateException {
		this.sendBuzz(to);
	}

	@Override
	public void sendTypingNotification(Contact contact, boolean isTyping) throws IOException {
		this.sendTypingNotification(contact, isTyping);
	}

	@Override
	public void setStatus(Status status) throws IllegalArgumentException {
		this.status.setStatus(status);
	}

	@Override
	public void setStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		this.status.setStatus(message, showBusyIcon);
	}

	@Override
	public Conference createConference(String conferenceId, Set<String> invitedIds, String message) {
		return this.conference.createConference(conferenceId, invitedIds, message);
	}

	@Override
	public void sendConferenceMessage(Conference conference, String message) throws IllegalArgumentException {
		this.conference.sendConferenceMessage(conference, message);
	}

	@Override
	public void leaveConference(Conference conference) throws IllegalArgumentException {
		this.conference.leaveConference(conference);
	}

	@Override
	public void acceptConferenceInvite(Conference conference) throws IllegalArgumentException {
		this.conference.acceptConferenceInvite(conference);
	}

	@Override
	public void declineConferenceInvite(Conference conference, String message) throws IllegalArgumentException {
		this.conference.declineConferenceInvite(conference, message);
	}

	@Override
	public void extendConference(Conference conference, String invitedId, String message)
			throws IllegalArgumentException {
		this.conference.extendConference(conference, invitedId, message);
	}

	@Override
	public Conference getConference(String conferenceId) {
		return this.conference.getConference(conferenceId);
	}

	@Override
	public ConferenceStatus getConferenceStatus(String conferenceId) {
		return this.conference.getConferenceStatus(conferenceId);
	}

}
