package org.openymsg.contact;

import java.util.Set;

public interface ContactListener {

	void addContacts(Set<ContactImpl> usersOnFriendsList);

	void addIgnoredFriends(Set<ContactImpl> usersOnIgnoreList);

	//TODO - should be status
	void addPendingFriends(Set<ContactImpl> usersOnPendingList);

}
