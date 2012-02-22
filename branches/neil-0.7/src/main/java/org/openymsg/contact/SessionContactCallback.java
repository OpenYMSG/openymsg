package org.openymsg.contact;

import java.util.Set;

import org.openymsg.Contact;

public interface SessionContactCallback {

	void addedContacts(Set<Contact> contacts);

//	void contactAddSuccess(Contact contact);
	
	void contactAddFailure(Contact contact, ContactAddFailure failure, String additionalInformation);
}
