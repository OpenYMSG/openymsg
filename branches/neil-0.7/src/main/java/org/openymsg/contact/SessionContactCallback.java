package org.openymsg.contact;

import java.util.Set;

import org.openymsg.Contact;

public interface SessionContactCallback {

	void addContacts(Set<Contact> usersOnFriendsList);

	void addIgnored(Set<Contact> usersOnIgnoreList);

	//TODO - should be status
	void addPending(Set<Contact> usersOnPendingList);

}
