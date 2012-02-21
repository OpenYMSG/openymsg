package org.openymsg.contact;

import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;

public interface SessionContactCallback {

	void addContacts(Set<Contact> usersOnFriendsList);

	void addIgnored(Set<Contact> usersOnIgnoreList);

	void addGroups(Set<ContactGroup> contactGroups);
	
	//TODO - should be status
	void addPending(Set<Contact> usersOnPendingList);

}
