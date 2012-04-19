package org.openymsg;

import java.util.Set;

import org.openymsg.auth.SessionAuthentication;
import org.openymsg.auth.SessionAuthenticationImpl;
import org.openymsg.conference.SessionConference;
import org.openymsg.conference.SessionConferenceImpl;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.connection.ConnectionState;
import org.openymsg.connection.SessionConnectionCallback;
import org.openymsg.connection.SessionConnectionImpl;
import org.openymsg.contact.SessionContactImpl;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.mail.SessionMail;
import org.openymsg.mail.SessionMailImpl;
import org.openymsg.message.SessionMessage;
import org.openymsg.message.SessionMessageImpl;
import org.openymsg.session.SessionSession;
import org.openymsg.session.SessionSessionImpl;
import org.openymsg.unknown.SessionUnknown;

public class SessionImpl implements YahooSession {
	private SessionConfig config;
	private SessionConnectionImpl connection;
	private YahooSessionCallback callback;
	private SessionSession session;
	private SessionMessage message;
	private SessionAuthentication authorize;
	private SessionContactImpl contact;
	private SessionConference conference;
	@SuppressWarnings("unused")
	private SessionUnknown unknown;
	@SuppressWarnings("unused")
	private SessionMail mail;

	public SessionImpl(SessionConfig config, YahooSessionCallback callback) {
		this.config = config;
		this.callback = callback;
	}

	/**
	 * {@inheritDoc} Initialize the connection and login to yahoo and return immediately.
	 */
	@Override
	public void login(String username, String password) throws IllegalArgumentException, IllegalStateException {
		this.initialize(username);
		this.authorize.login(username, password);
	}

	private void initialize(String username) {
		ExecutorImpl executor = new ExecutorImpl(username);
		this.connection = new SessionConnectionImpl(executor);
		this.connection.initialize(config);
		this.authorize = new SessionAuthenticationImpl(config, executor, callback);
		this.session = new SessionSessionImpl(username, executor, callback);
		this.contact = new SessionContactImpl(executor, username, callback);
		this.message = new SessionMessageImpl(executor, username, callback);
		this.conference = new SessionConferenceImpl(username, executor, callback);
		this.mail = new SessionMailImpl(executor);
		this.unknown = new SessionUnknown(executor);
	}

	@Override
	public void logout() {
		this.session.logout();
	}

	@Override
	public void sendMessage(YahooContact contact, String message) throws IllegalStateException {
		// TODO - check status
		// checkStatus();
		// TODO current contact?
		this.message.sendMessage(contact, message);
	}

	@Override
	public void sendBuzz(YahooContact to) throws IllegalStateException {
		this.message.sendBuzz(to);
	}

	@Override
	public void sendTypingNotification(YahooContact contact, boolean isTyping) {
		this.message.sendTypingNotification(contact, isTyping);
	}

	@Override
	public void setStatus(YahooStatus status) throws IllegalArgumentException {
		this.session.setStatus(status);
	}

	@Override
	public void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		this.session.setCustomStatus(message, showBusyIcon);
	}

	@Override
	public YahooConference createConference(String conferenceId, Set<YahooContact> contacts, String message) {
		return this.conference.createConference(conferenceId, contacts, message);
	}

	@Override
	public void sendConferenceMessage(YahooConference conference, String message) throws IllegalArgumentException {
		this.conference.sendConferenceMessage(conference, message);
	}

	@Override
	public void leaveConference(YahooConference conference) throws IllegalArgumentException {
		this.conference.leaveConference(conference);
	}

	@Override
	public void acceptConferenceInvite(YahooConference conference) throws IllegalArgumentException {
		this.conference.acceptConferenceInvite(conference);
	}

	@Override
	public void declineConferenceInvite(YahooConference conference, String message) throws IllegalArgumentException {
		this.conference.declineConferenceInvite(conference, message);
	}

	@Override
	public void extendConference(YahooConference conference, YahooContact contact, String message)
			throws IllegalArgumentException {
		this.conference.extendConference(conference, contact, message);
	}

	@Override
	public YahooConference getConference(String conferenceId) {
		return this.conference.getConference(conferenceId);
	}

	@Override
	public YahooConferenceStatus getConferenceStatus(String conferenceId) {
		return this.conference.getConferenceStatus(conferenceId);
	}

	@Override
	public void acceptFriendAuthorization(YahooContact contact) throws IllegalStateException {
		this.contact.acceptFriendAuthorization(contact);
	}

	@Override
	public void rejectFriendAuthorization(YahooContact contact, String message) throws IllegalStateException {
		this.contact.rejectFriendAuthorization(contact, message);
	}

	@Override
	public void removeFromGroup(YahooContact contact, YahooContactGroup group) {
		this.contact.removeFromGroup(contact, group);
	}

	@Override
	public void addContact(YahooContact contact, YahooContactGroup group) throws IllegalArgumentException {
		this.contact.addContact(contact, group);
	}

	@Override
	public Set<YahooContact> getContacts() {
		return this.contact.getContacts();
	}

	@Override
	public Set<YahooContactGroup> getContactGroups() {
		return this.contact.getContactGroups();
	}

	@Override
	public ConnectionState getConnectionState() {
		return this.connection.getConnectionState();
	}

	@Override
	public ConnectionInfo getConnectionInfo() {
		return this.connection.getConnectionInfo();
	}

	@Override
	public void addListener(SessionConnectionCallback listener) {
		this.connection.addListener(listener);
	}

	@Override
	public boolean removeListener(SessionConnectionCallback listener) {
		return this.connection.removeListener(listener);
	}

	// @Override
	// public void addListener(SessionAuthenticationCallback listener) {
	// this.authorize.addListener(listener);
	// }
	//
	// @Override
	// public boolean removeListener(SessionAuthenticationCallback listener) {
	// return this.authorize.removeListener(listener);
	// }

	@Override
	public void addGroup(String groupName) {
		this.contact.addGroup(groupName);
	}

	@Override
	public YahooContactStatus getStatus(YahooContact contact) {
		return this.contact.getStatus(contact);
	}

	@Override
	public Set<YahooConference> getConferences() {
		return this.conference.getConferences();
	}
}
