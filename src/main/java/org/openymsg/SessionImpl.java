package org.openymsg;

import org.openymsg.conference.SessionConference;
import org.openymsg.conference.SessionConferenceImpl;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.connection.ConnectionState;
import org.openymsg.connection.SessionConnectionImpl;
import org.openymsg.connection.YahooConnection;
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

import java.util.Set;

public class SessionImpl implements YahooSession {
	private final SessionConfig config;
	protected YahooConnection connection;
	protected final YahooSessionCallback callback;
	private SessionContextImpl context;
	protected SessionMessage message;
	private SessionContactImpl contact;
	private SessionConference conference;
	@SuppressWarnings("unused")
	private SessionUnknown unknown;
	@SuppressWarnings("unused")
	private SessionMail mail;
	private YahooSessionState state;
	protected ExecutorImpl executor;

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
		this.connection = createConnection(executor, callback, config);
		this.contact = new SessionContactImpl(connection, username, callback);
		this.context = new SessionContextImpl(config, executor, connection, username, callback);
		initializeSessionMessage(username);
		this.conference = new SessionConferenceImpl(username, connection, callback);
		this.mail = new SessionMailImpl(connection);
		this.unknown = new SessionUnknown(connection);
		// TODO Why register here?
		connection.register(ServiceType.LOGOFF, new PagerLogoffResponse(username, context, contact));
	}

	protected YahooConnection createConnection(ExecutorImpl executor, YahooSessionCallback callback,
			SessionConfig config) {
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(executor, callback);
		sessionConnection.initialize(config);
		return sessionConnection;
	}

	@Override
	public void logout() throws IllegalStateException {
		if (!state.isLoggedIn() || !state.isFailure()) {
			// TODO - nah - log this or send exception("Session in wrong state: " + state);
		}
		context.logout();
		// no event from yahoo anymore
		state = YahooSessionState.LOGGED_OUT;
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
		message.sendBuzz(to);
	}

	@Override
	public void sendTypingNotification(YahooContact contact, boolean isTyping) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		message.sendTypingNotification(contact, isTyping);
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
		context.setCustomStatus(message, showBusyIcon);
	}

	@Override
	public YahooConference createConference(String conferenceId, Set<YahooContact> contacts, String message) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return conference.createConference(conferenceId, contacts, message);
	}

	@Override
	public void sendConferenceMessage(YahooConference conference, String message)
			throws IllegalArgumentException, IllegalStateException {
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
	public void acceptConferenceInvite(YahooConference conference)
			throws IllegalArgumentException, IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.conference.acceptConferenceInvite(conference);
	}

	@Override
	public void declineConferenceInvite(YahooConference conference, String message)
			throws IllegalArgumentException, IllegalStateException {
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
		return conference.getConference(conferenceId);
	}

	@Override
	public void acceptFriendAuthorization(String id, YahooContact contact) throws IllegalStateException {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		this.contact.acceptFriendAuthorization(id, contact);
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
		return contact.getContacts();
	}

	@Override
	public Set<YahooContactGroup> getContactGroups() {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		return contact.getContactGroups();
	}

	@Override
	public ConnectionState getConnectionState() {
		return connection.getConnectionState();
	}

	@Override
	public ConnectionInfo getConnectionInfo() {
		return connection.getConnectionInfo();
	}

	@Override
	public void addGroup(String groupName) {
		if (!state.isAvailable()) {
			throw new IllegalStateException("Session in wrong state: " + state);
		}
		contact.addGroup(groupName);
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
		return conference.getConferences();
	}

	@Override
	public AuthenticationFailure getFailureState() {
		return context.getFailureState();
	}

	public void connectionSuccessful() {
		state = YahooSessionState.CONNECTED;
	}

	public void connectionFailed() {
		state = YahooSessionState.FAILURE;
	}

	public void connectionPrematurelyEnded() {
		state = YahooSessionState.FAILURE;
	}

	public void loggedOfNormally() {
		state = YahooSessionState.LOGGED_OUT;
		connection.shutdown();
	}

	public void loggedOffForced() {
		state = YahooSessionState.FAILURE;
		connection.shutdown();
	}

	public void failedAuthentication() {
		state = YahooSessionState.FAILURE;
		connection.shutdown();
	}

	@Override
	public boolean isShutdown() {
		return executor.isTerminated();
	}

	@Override
	public boolean isDisconnected() {
		return !connection.getConnectionState().isConnected();
	}

	@Override
	public void renameGroup(YahooContactGroup group, String newName) throws IllegalArgumentException {
		contact.renameGroup(group, newName);
	}

	public void authenticationSuccess() {
		state = YahooSessionState.LOGGED_IN;
	}

	protected void initializeSessionMessage(String username) {
		message = new SessionMessageImpl(connection, username, callback);
	}

	@Override
	public void keepAlive() {
		context.keepAlive();
	}
}
