package org.openymsg.contact.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Contact;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

/**
 * Handles several kinds of status messages. Status15 - single messages for Yahoo 9 users logging in and out/invisible
 * for MSN users Y6 Status Update for Yahoo 9 users changing status
 * @author neilhart
 */
public class SessionStatusImpl implements SessionStatus {
	private static final Log log = LogFactory.getLog(SessionStatusImpl.class);
	private Executor executor;
	private Map<Contact, ContactStatusImpl> statuses = new HashMap<Contact, ContactStatusImpl>();

	public SessionStatusImpl(Executor executor) {
		this.executor = executor;
		SingleStatusResponse singleStatusResponse = new SingleStatusResponse(this);
		this.executor.register(ServiceType.STATUS_15, singleStatusResponse);
		this.executor.register(ServiceType.Y6_STATUS_UPDATE, singleStatusResponse);
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
