package org.openymsg;

import java.io.IOException;
import java.util.Set;

import org.openymsg.auth.SessionAuthentication;
import org.openymsg.auth.SessionAuthenticationCallback;
import org.openymsg.auth.SessionAuthenticationImpl;
import org.openymsg.conference.SessionConference;
import org.openymsg.conference.SessionConferenceImpl;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.connection.ConnectionState;
import org.openymsg.connection.SessionConnectionCallback;
import org.openymsg.connection.SessionConnectionImpl;
import org.openymsg.contact.SessionContact;
import org.openymsg.contact.SessionContactImpl;
import org.openymsg.execute.dispatch.ExecutorImpl;
import org.openymsg.group.SessionGroup;
import org.openymsg.group.SessionGroupImpl;
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
	private SessionConnectionImpl connection;
	private SessionCallback callback;
	private SessionSession session;
	private SessionMessage message;
	private SessionAuthentication authorize;
	private SessionContact contact;
	private SessionGroup group;
	private SessionStatus status;
	private SessionConference conference;
	@SuppressWarnings("unused")
	private SessionKeepAlive keepAlive;
	@SuppressWarnings("unused")
	private SessionUnknown unknown;
	@SuppressWarnings("unused")
	private SessionMail mail;

	public SessionImpl(SessionConfig config, SessionCallback callback) {
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
		this.connection.initialize(this.config);
		this.authorize = new SessionAuthenticationImpl(this.config, executor);
		this.session = new SessionSessionImpl(username, executor);
		this.contact = new SessionContactImpl(executor, username);
		this.group = new SessionGroupImpl();
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
	public Conference createConference(String conferenceId, Set<Contact> contacts, String message) {
		return this.conference.createConference(conferenceId, contacts, message);
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
	public void extendConference(Conference conference, Contact contact, String message)
			throws IllegalArgumentException {
		this.conference.extendConference(conference, contact, message);
	}

	@Override
	public Conference getConference(String conferenceId) {
		return this.conference.getConference(conferenceId);
	}

	@Override
	public ConferenceStatus getConferenceStatus(String conferenceId) {
		return this.conference.getConferenceStatus(conferenceId);
	}

	@Override
	public void acceptFriendAuthorization(Contact contact) throws IllegalStateException {
		this.contact.acceptFriendAuthorization(contact);
	}

	@Override
	public void rejectFriendAuthorization(Contact contact, String message) throws IllegalStateException {
		this.contact.rejectFriendAuthorization(contact, message);
	}

	@Override
	public void removeFromGroup(Contact contact, ContactGroup group) {
		this.contact.removeFromGroup(contact, group);
	}

	@Override
	public void addContact(Contact contact, ContactGroup group) throws IllegalArgumentException {
		this.contact.addContact(contact, group);
	}

	@Override
	public Set<Contact> getContacts() {
		return this.contact.getContacts();
	}

	@Override
	public Set<ContactGroup> getContactGroups() {
		return this.group.getContactGroups();
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

	@Override
	public void addListener(SessionAuthenticationCallback listener) {
		this.authorize.addListener(listener);
	}

	@Override
	public boolean removeListener(SessionAuthenticationCallback listener) {
		return this.authorize.removeListener(listener);
	}

}
