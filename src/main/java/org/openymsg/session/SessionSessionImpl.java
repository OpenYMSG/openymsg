package org.openymsg.session;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Status;
import org.openymsg.execute.Executor;
import org.openymsg.execute.write.ScheduledMessageSender;
import org.openymsg.network.ServiceType;

public class SessionSessionImpl implements SessionSession {
	private static final Log log = LogFactory.getLog(SessionSessionImpl.class);
	private String username;
	private Executor executor;

	public SessionSessionImpl(String username, Executor executor) {
		this.username = username;
		this.executor = executor;
		this.executor.register(ServiceType.LIST, new ListResponse());
		this.executor.register(ServiceType.LOGOFF, new PagerLogoffResponse());
		this.executor.register(ServiceType.PING, new PingResponse());
		this.executor.schedule(new ScheduledMessageSender(this.executor, new PingRequest()), (60 * 60 * 1000));
		this.executor.schedule(new ScheduledMessageSender(this.executor, new KeepAliveRequest(username)), (60 * 1000));
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
	 * Logs off the current session.
	 */
	@Override
	public void logout() {
		log.trace("logout: " + username);
		// TODO - move status check to session
		// ConnectionState executionState = this.executor.getState();
		// if (executionState.isConnected()) {
		executor.execute(new LogoutMessage(username));
		// }
		// else {
		// log.info("Trying to logout when not connected: " + username);
		// }
		this.executor.execute(new ShutdownRequest(this.executor));
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. If you want to login as invisible, set
	 * this to Status.INVISIBLE before you call login(). Note: this setter is overloaded. The second version is intended
	 * for use when setting custom status messages.
	 * @param status The new Status to be set for this user.
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void setStatus(Status status) throws IllegalArgumentException {
		log.debug("setting status: " + status);
		if (status == Status.CUSTOM) {
			throw new IllegalArgumentException("Cannot set custom state without message");
		}
		this.executor.execute(new StatusChangeRequest(status));
		// TODO set internal status
		// this.status = status;
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
	 * @throws IOException
	 */
	public synchronized void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		// TODO - check status
		// if (sessionStatus == SessionState.UNSTARTED) {
		// throw new IllegalArgumentException("Unstarted sessions can be available or invisible only");
		// }
		//
		if (message == null) {
			throw new IllegalArgumentException("Cannot set custom state with null message");
		}
		// TODO set internal status
		// status = Status.CUSTOM;
		// customStatusMessage = message;
		// customStatusBusy = showBusyIcon;

		// TODO - handle showBusy
		this.executor.execute(new StatusChangeRequest(Status.CUSTOM, message));
	}

}
