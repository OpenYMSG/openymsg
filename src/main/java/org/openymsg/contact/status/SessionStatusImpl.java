package org.openymsg.contact.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Contact;
import org.openymsg.Status;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionStatusImpl implements SessionStatus {
	private static final Log log = LogFactory.getLog(SessionStatusImpl.class);
	private Executor executor;
	private Map<Contact, ContactStatusImpl> statuses = new HashMap<Contact, ContactStatusImpl>();

	public SessionStatusImpl(Executor executor) {
		this.executor = executor;
		this.executor.register(ServiceType.STATUS_15, new ListOfStatusesResponse(this));
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
	public synchronized void setStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
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

	public ContactStatusImpl getStatus(Contact contact) {
		return this.statuses.get(contact);
	}

	public void addStatus(Contact contact, ContactStatusImpl status) {
		log.info("Status change for: " + contact + " " + status);
		this.statuses.put(contact, status);
	}

	public void addPending(Contact contact) {
		// TODO Auto-generated method stub

	}

	public void publishPending(Contact contact) {
		// TODO Auto-generated method stub

	}

	public void addedIgnored(Set<Contact> usersOnIgnoreList) {
		for (Contact contact : usersOnIgnoreList) {
			System.err.println("ignored: " + contact);
		}
	}

	public void addedPending(Set<Contact> usersOnPendingList) {
		// for (Contact contact : usersOnPendingList) {
		// System.err.println("pending:" + contactImpl.getId() + "/" + contactImpl.getProtocol());
		// }
	}

}
