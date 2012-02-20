package org.openymsg.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Status;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionStatusImpl implements SessionStatus {
	private static final Log log = LogFactory.getLog(SessionStatusImpl.class);
	private Executor executor;
	private Map<String, ContactStatusImpl> statuses = new HashMap<String, ContactStatusImpl>();

	public SessionStatusImpl(Executor executor) {
		this.executor = executor;
		this.executor.register(ServiceType.STATUS_15, new ListOfStatusesResponse(this));
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. If you want to login as invisible, set
	 * this to Status.INVISIBLE before you call login().
	 * 
	 * Note: this setter is overloaded. The second version is intended for use when setting custom status messages.
	 * 
	 * @param status
	 *            The new Status to be set for this user.
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void setStatus(Status status) throws IllegalArgumentException {
		log.debug("setting status: " + status);
		if (status == Status.CUSTOM) {
			throw new IllegalArgumentException("Cannot set custom state without message");
		}
		this.executor.execute(new StatusChangeRequest(status));
//TODO set internal status
//		this.status = status;
//		customStatusMessage = null;
		// TODO - Check status
		// if (sessionStatus != SessionState.UNSTARTED) {
		// }
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. Legit values are in the StatusConstants
	 * interface. If you want to login as invisible, set this to Status.INVISIBLE before you call login() Note: setter
	 * is overloaded, the second version is intended for use when setting custom status messages. The boolean is true if
	 * available and false if away.
	 * 
	 * @param message
	 * @param showBusyIcon
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public synchronized void setStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		// TODO - check status
		// if (sessionStatus == SessionState.UNSTARTED) {
		// throw new IllegalArgumentException("Unstarted sessions can be available or invisible only");
		// }
		//
		if (message == null) {
			throw new IllegalArgumentException("Cannot set custom state with null message");
		}
//TODO set internal status
//		status = Status.CUSTOM;
//		customStatusMessage = message;
//		customStatusBusy = showBusyIcon;

		//TODO - handle showBusy
		this.executor.execute(new StatusChangeRequest(Status.CUSTOM, message));
	}

	public ContactStatusImpl getStatus(String userId) {
		return this.statuses.get(userId);
	}

	public void addStatus(String userId, ContactStatusImpl status) {
		log.info("Status change for: " + userId + " " + status);
		this.statuses.put(userId, status);
	}

}
