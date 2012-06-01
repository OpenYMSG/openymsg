package org.openymsg;

import java.util.Set;

import org.openymsg.conference.SessionConference;
import org.openymsg.conference.SessionConferenceImpl;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.connection.ConnectionState;
import org.openymsg.connection.SessionConnectionImpl;
import org.openymsg.contact.SessionContactImpl;
import org.openymsg.context.SessionCallbackHandler;
import org.openymsg.context.SessionContextImpl;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.context.session.PagerLogoffResponse;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.mail.SessionMail;
import org.openymsg.mail.SessionMailImpl;
import org.openymsg.message.SessionMessage;
import org.openymsg.message.SessionMessageImpl;
import org.openymsg.network.ServiceType;
import org.openymsg.unknown.SessionUnknown;

public class SessionImpl implements YahooSession {
	private SessionConfig config;
	protected SessionConnectionImpl connection;
	protected YahooSessionCallback callback;
	private SessionContextImpl context;
	protected SessionMessage message;
	private SessionContactImpl contact;
	private SessionConference conference;
	@SuppressWarnings("unused")
	private SessionUnknown unknown;
	@SuppressWarnings("unused")
	private SessionMail mail;
	private YahooSessionState state;
	private ExecutorImpl executor;

	public SessionImpl(SessionConfig config, YahooSessionCallback callback) {
		this.config = config;
		this.callback = new SessionCallbackHandler(this, callback);
		this.state = YahooSessionState.NOT_STARTED;
	}

	/**
	 * {@inheritDoc} Initialize the connection and login to yahoo and return immediately.
	 */
	@Override
	public void login(String username, String password) throws IllegalArgumentException, IllegalStateException {
		if (!this.state.isReadyToStart()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.state = YahooSessionState.STARTED;
		this.initialize(username);
		this.context.login(username, password);
	}

	private void initialize(String username) {
		this.executor = new ExecutorImpl(username);
		this.connection = new SessionConnectionImpl(executor, callback);
		this.connection.initialize(config);
		this.contact = new SessionContactImpl(connection, username, callback);
		this.context = new SessionContextImpl(config, executor, connection, username, callback);
		initializeSessionMessage(username);
		this.conference = new SessionConferenceImpl(username, connection, callback);
		this.mail = new SessionMailImpl(connection);
		this.unknown = new SessionUnknown(connection);
		connection.register(ServiceType.LOGOFF, new PagerLogoffResponse(username, context, contact));
	}

	@Override
	public void logout() throws IllegalStateException {
		if (!state.isLoggedIn()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.context.logout();
	}

	@Override
	public void sendMessage(YahooContact contact, String message) throws IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.message.sendMessage(contact, message);
	}

	@Override
	public void sendBuzz(YahooContact to) throws IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.message.sendBuzz(to);
	}

	@Override
	public void sendTypingNotification(YahooContact contact, boolean isTyping) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.message.sendTypingNotification(contact, isTyping);
	}

	@Override
	public void setStatus(YahooStatus status) throws IllegalArgumentException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.context.setStatus(status);
	}

	@Override
	public void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.context.setCustomStatus(message, showBusyIcon);
	}

	@Override
	public YahooConference createConference(String conferenceId, Set<YahooContact> contacts, String message) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return this.conference.createConference(conferenceId, contacts, message);
	}

	@Override
	public void sendConferenceMessage(YahooConference conference, String message) throws IllegalArgumentException,
			IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.conference.sendConferenceMessage(conference, message);
	}

	@Override
	public void leaveConference(YahooConference conference) throws IllegalArgumentException, IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.conference.leaveConference(conference);
	}

	@Override
	public void acceptConferenceInvite(YahooConference conference) throws IllegalArgumentException,
			IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.conference.acceptConferenceInvite(conference);
	}

	@Override
	public void declineConferenceInvite(YahooConference conference, String message) throws IllegalArgumentException,
			IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.conference.declineConferenceInvite(conference, message);
	}

	@Override
	public void extendConference(YahooConference conference, Set<YahooContact> contacts, String message)
			throws IllegalArgumentException, IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.conference.extendConference(conference, contacts, message);
	}

	@Override
	public YahooConference getConference(String conferenceId) throws IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return this.conference.getConference(conferenceId);
	}

	// @Override
	// public YahooConferenceStatus getConferenceStatus(String conferenceId) {
	// if (!state.isAvailable()) {
	// throw new IllegalStateException("Session in wrong state: " + state);
	// }
	// return this.conference.getConferenceStatus(conferenceId);
	// }

	@Override
	public void acceptFriendAuthorization(YahooContact contact) throws IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.contact.acceptFriendAuthorization(contact);
	}

	@Override
	public void rejectFriendAuthorization(YahooContact contact, String message) throws IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.contact.rejectFriendAuthorization(contact, message);
	}

	@Override
	public void removeFromGroup(YahooContact contact, YahooContactGroup group) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.contact.removeFromGroup(contact, group);
	}

	@Override
	public void addContact(YahooContact contact, YahooContactGroup group, String message)
			throws IllegalArgumentException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.contact.addContact(contact, group, message);
	}

	@Override
	public Set<YahooContact> getContacts() {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return this.contact.getContacts();
	}

	@Override
	public Set<YahooContactGroup> getContactGroups() {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
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

	// @Override
	// public void addListener(SessionConnectionCallback listener) {
	// this.connection.addListener(listener);
	// }
	//
	// @Override
	// public boolean removeListener(SessionConnectionCallback listener) {
	// return this.connection.removeListener(listener);
	// }

	@Override
	public void addGroup(String groupName) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.contact.addGroup(groupName);
	}

	@Override
	public YahooContactStatus getStatus(YahooContact contact) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return this.contact.getStatus(contact);
	}

	@Override
	public Set<YahooConference> getConferences() {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return this.conference.getConferences();
	}

	@Override
	public AuthenticationFailure getFailureState() {
		return context.getFailureState();
	}

	public void connectionSuccessful() {
		this.state = YahooSessionState.CONNECTED;
	}

	public void connectionFailed() {
		this.state = YahooSessionState.FAILURE;
	}

	public void connectionPrematurelyEnded() {
		this.state = YahooSessionState.FAILURE;
	}

	public void loggedOfNormally() {
		this.state = YahooSessionState.LOGGED_OUT;
		this.connection.shutdown();
	}

	public void loggedOffForced() {
		this.state = YahooSessionState.FAILURE;
		this.connection.shutdown();
	}

	public void failedAuthentication() {
		this.state = YahooSessionState.FAILURE;
		this.connection.shutdown();
	}

	@Override
	public boolean isShutdown() {
		return this.executor.isTerminated();
	}

	@Override
	public boolean isDisconnected() {
		return !this.connection.getConnectionState().isConnected();
	}

	@Override
	public void renameGroup(YahooContactGroup group, String newName) throws IllegalArgumentException {
		this.contact.renameGroup(group, newName);
	}

	public void authenticationSuccess() {
		this.state = YahooSessionState.LOGGED_IN;
	}

	protected void initializeSessionMessage(String username) {
		this.message = new SessionMessageImpl(connection, username, callback);
	}

}
