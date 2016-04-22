package org.openymsg.contact.status;

import java.util.HashMap;
import java.util.Map;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;

public class ContactStatusState {
	protected final Map<YahooContact, YahooContactStatus> statuses = new HashMap<YahooContact, YahooContactStatus>();

	public void putStatus(YahooContact contact, YahooContactStatus status) {
		statuses.put(contact, status);
	}

	public YahooContactStatus getStatus(YahooContact contact) {
		return statuses.get(contact);
	}

}
