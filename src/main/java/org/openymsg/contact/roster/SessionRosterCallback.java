package org.openymsg.contact.roster;

import org.openymsg.Contact;
import org.openymsg.Name;

public interface SessionRosterCallback {

	void addedContact(Contact contacts);

	void removedContact(Contact contacts);

	// void contactAddSuccess(Contact contact);

	void contactAddFailure(Contact contact, ContactAddFailure failure, String additionalInformation);

	void contactAddAccepted(Contact contact);

	void contactAddDeclined(Contact contact, String message);

	void contactAddRequest(Contact contact, Name name, String message);
}
