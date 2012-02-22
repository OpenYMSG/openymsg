package org.openymsg.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Executor;
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

}
