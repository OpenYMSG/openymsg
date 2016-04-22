package org.openymsg.contact.status;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;

public class ContactStatusSocketService implements SessionStatusCallback, ContactStatusChangeCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(ContactStatusSocketService.class);
	private final SessionStatusCallback callback;
	private final ContactStatusState state;

	public ContactStatusSocketService(SessionStatusCallback callback, ContactStatusState state) {
		this.callback = callback;
		this.state = state;
	}

	@Override
	public void statusUpdate(YahooContact contact, YahooContactStatus status) {
		log.trace("statusUpdate: " + contact + " " + status);
		state.putStatus(contact, status);
		callback.statusUpdate(contact, status);
	}

	@Override
	public void receivedContactLogoff(YahooContact contact) {
		this.statusUpdate(contact, ContactStatusImpl.OFFLINE);
	}

	@Override
	public void addPending(YahooContact contact) {
		log.trace("addPending: " + contact);
		YahooContactStatus status = ContactStatusImpl.PENDING;
		state.putStatus(contact, status);
		this.callback.statusUpdate(contact, status);
	}

	@Override
	public void publishPending(YahooContact contact) {
		log.trace("publishPending: " + contact);
		YahooContactStatus status = ContactStatusImpl.PENDING;
		state.putStatus(contact, status);
		this.callback.statusUpdate(contact, status);
	}

	@Override
	public void addedIgnored(Set<YahooContact> usersOnIgnoreList) {
		for (YahooContact contact : usersOnIgnoreList) {
			log.error("ignored: " + contact);
		}
	}

	@Override
	public void addedPending(Set<YahooContact> usersOnPendingList) {
		log.trace("publishPending: " + usersOnPendingList);
		YahooContactStatus status = ContactStatusImpl.PENDING;
		for (YahooContact contact : usersOnPendingList) {
			state.putStatus(contact, status);
			this.callback.statusUpdate(contact, status);
		}
		// for (Contact contact : usersOnPendingList) {
		// System.err.println("pending:" + contactImpl.getId() + "/" +
		// contactImpl.getProtocol());
		// }
	}
}
