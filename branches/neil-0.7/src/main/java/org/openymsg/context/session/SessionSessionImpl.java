package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooStatus;
import org.openymsg.execute.Executor;
import org.openymsg.execute.write.ScheduledMessageSender;
import org.openymsg.network.ServiceType;

public class SessionSessionImpl implements SessionSession {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionSessionImpl.class);
	private String username;
	private Executor executor;
	private SessionSessionCallback callback;
	private LoginState state;

	public SessionSessionImpl(String username, Executor executor, SessionSessionCallback callback) {
		if (executor == null) {
			throw new IllegalArgumentException("Executor cannot be null");
		}
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		this.username = username;
		this.executor = executor;
		this.callback = callback;
		state = LoginState.LOGGING_IN;
		executor.register(ServiceType.LIST, new ListResponse());
		executor.register(ServiceType.LOGOFF, new PagerLogoffResponse(username, this));
		executor.register(ServiceType.PING, new PingResponse());
	}

	// TODO - looks cool
	// /**
	// * Ask server to return refreshed stats for this session. Server will send back a USERSTAT and truncated NEWMAIL
	// * packet.
	// */
	// public void refreshStats() throws IllegalStateException, IOException {
	// checkStatus();
	// transmitUserStat();
	// }

	/**
	 * Notify session that login is complete
	 */
	public void loginComplete() {
		if (!state.isLoggingIn()) {
			throw new IllegalStateException("State is not logging in: " + state);
		}
		state = LoginState.LOGGED_IN;
		executor.schedule(new ScheduledMessageSender(executor, new PingMessage()), (60 * 60 * 1000));
		executor.schedule(new ScheduledMessageSender(executor, new KeepAliveMessage(username)), (60 * 1000));
	}

	/**
	 * Logs off the current session.
	 */
	@Override
	public void logout() {
		log.trace("logout: " + username);
		if (!state.isLoggedIn()) {
			throw new IllegalStateException("State is not logging in: " + state);
		}
		state = LoginState.LOGGING_OUT;

		executor.execute(new LogoutMessage(username));

		// TODO schedule this incase no response from yahoo, not here
		executor.execute(new ShutdownRequest(executor));
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. If you want to login as invisible, set
	 * this to Status.INVISIBLE before you call login(). Note: this setter is overloaded. The second version is intended
	 * for use when setting custom status messages.
	 * @param status The new Status to be set for this user.
	 * @throws IllegalArgumentException
	 */
	public void setStatus(YahooStatus status) throws IllegalArgumentException {
		log.debug("setting status: " + status);
		if (status == YahooStatus.CUSTOM) {
			throw new IllegalArgumentException("Cannot set custom state without message");
		}
		executor.execute(new StatusChangeRequest(status));
		// TODO set internal status
		// status = status;
		// customStatusMessage = null;
		// TODO - Check status
		// if (sessionStatus != SessionState.UNSTARTED) {
		// }
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. Legit values are in the StatusConstants
	 * interface. If you want to login as invisible, set this to Status.INVISIBLE before you call login() Note: setter
	 * is overloaded, the second version is intended for use when setting custom status messages. The boolean is true if
	 * available and false if away.
	 * @param message
	 * @param showBusyIcon
	 * @throws IllegalArgumentException
	 */
	// TODO showBusyIcon isn't used
	public synchronized void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		if (message == null) {
			throw new IllegalArgumentException("Cannot set custom state with null message");
		}
		// TODO set internal status
		// status = Status.CUSTOM;
		// customStatusMessage = message;
		// customStatusBusy = showBusyIcon;

		// TODO - handle showBusy
		executor.execute(new StatusChangeRequest(YahooStatus.CUSTOM, message));
	}

	public void receivedLogout(LogoutReason reason) {
		log.info("receivedLogout: " + state + " with state: " + state);
		if (reason == null && state.isLoggingOff()) {
			// TODO I don't think this happens
			callback.logoffNormalComplete();
		} else {
			callback.logoffForced(reason);
		}

		// switch (state) {
		// case UNKNOWN_52:
		// log.info("AUTHRESP says: Logged off with " + state);
		// session.setState(state);
		// break;
		//
		// case DUPLICATE_LOGIN1:
		// case DUPLICATE_LOGIN2:
		// log.info("AUTHRESP says: Logged off with Duplicate Login: " + state);
		// session.setState(state);
		// break;
		// default:
		// log.warn("AUTHRESP says: logged off with an unchecked reason: " + state);
		// session.setState(state);
		// }

	}

}
