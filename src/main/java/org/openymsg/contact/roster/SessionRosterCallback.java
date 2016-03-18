package org.openymsg.contact.roster;

import org.openymsg.Name;
import org.openymsg.YahooContact;

public interface SessionRosterCallback {
	void rosterLoaded();

	void addedContact(YahooContact contact);

	void removedContact(YahooContact contact);

	// void contactAddSuccess(Contact contact);
	void receivedContactAddFailure(YahooContact contact, ContactAddFailure failure, String additionalInformation);

	void receivedContactAddAccepted(YahooContact contact);

	void receivedContactAddDeclined(YahooContact contact, String message);

	void receivedContactAddRequest(String id, YahooContact contact, Name name, String message);
}
