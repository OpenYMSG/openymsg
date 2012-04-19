package org.openymsg.contact.roster;

import org.openymsg.YahooContact;
import org.openymsg.Name;

public interface SessionRosterCallback {

	void rosterLoaded();

	void addedContact(YahooContact contact);

	void removedContact(YahooContact contact);

	// void contactAddSuccess(Contact contact);

	void contactAddFailure(YahooContact contact, ContactAddFailure failure, String additionalInformation);

	void contactAddAccepted(YahooContact contact);

	void contactAddDeclined(YahooContact contact, String message);

	void contactAddRequest(YahooContact contact, Name name, String message);
}
