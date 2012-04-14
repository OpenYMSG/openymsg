package org.openymsg.contact.roster;

import org.openymsg.Contact;
import org.openymsg.Name;

public interface SessionRosterCallback {

	void rosterLoaded();

	void addedContact(Contact contact);

	void removedContact(Contact contact);

	// void contactAddSuccess(Contact contact);

	void contactAddFailure(Contact contact, ContactAddFailure failure, String additionalInformation);

	void contactAddAccepted(Contact contact);

	void contactAddDeclined(Contact contact, String message);

	void contactAddRequest(Contact contact, Name name, String message);
}
