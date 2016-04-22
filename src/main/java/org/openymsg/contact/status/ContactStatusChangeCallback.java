package org.openymsg.contact.status;

import java.util.Set;

import org.openymsg.YahooContact;

public interface ContactStatusChangeCallback {

	void receivedContactLogoff(YahooContact contact);

	void addPending(YahooContact contact);

	void publishPending(YahooContact contact);

	void addedIgnored(Set<YahooContact> usersOnIgnoreList);

	void addedPending(Set<YahooContact> usersOnPendingList);

}
