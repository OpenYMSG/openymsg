package org.openymsg.contact.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;
import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;

/**
 * Handles several kinds of status messages. Status15 - single messages for Yahoo 9 users logging in and out/invisible
 * for MSN users Y6 Status Update for Yahoo 9 users changing status
 * @author neilhart
 */
public class SessionStatusImpl implements SessionStatus, SessionStatusCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionStatusImpl.class);
	private YahooConnection executor;
	private SessionStatusCallback callback;
	private Map<YahooContact, YahooContactStatus> statuses = new HashMap<YahooContact, YahooContactStatus>();

	public SessionStatusImpl(YahooConnection executor, SessionStatusCallback callback) {
		this.executor = executor;
		this.callback = callback;
		SingleStatusResponse singleStatusResponse = new SingleStatusResponse(this);
		this.executor.register(ServiceType.STATUS_15, new ListOfStatusesResponse(singleStatusResponse));
		this.executor.register(ServiceType.Y6_STATUS_UPDATE, singleStatusResponse);
	}

	public YahooContactStatus getStatus(YahooContact contact) {
		return this.statuses.get(contact);
	}

	@Override
	public void statusUpdate(YahooContact contact, YahooContactStatus status) {
		log.trace("statusUpdate: " + contact + " " + status);
		this.statuses.put(contact, status);
		this.callback.statusUpdate(contact, status);
	}

	public void addPending(YahooContact contact) {
		// TODO Auto-generated method stub

	}

	public void publishPending(YahooContact contact) {
		// TODO Auto-generated method stub

	}

	public void addedIgnored(Set<YahooContact> usersOnIgnoreList) {
		for (YahooContact contact : usersOnIgnoreList) {
			log.error("ignored: " + contact);
		}
	}

	public void addedPending(Set<YahooContact> usersOnPendingList) {
		// for (Contact contact : usersOnPendingList) {
		// System.err.println("pending:" + contactImpl.getId() + "/" + contactImpl.getProtocol());
		// }
	}

}
