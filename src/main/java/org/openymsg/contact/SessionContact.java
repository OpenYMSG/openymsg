package org.openymsg.contact;

import org.openymsg.Contact;

public interface SessionContact {

	void acceptFriendAuthorization(Contact contact) throws IllegalStateException;

	void rejectFriendAuthorization(Contact contact, String message) throws IllegalStateException;

	/**
	 * Instructs the Yahoo network to remove this friend from the particular group on the roster of the current user. If
	 * this is the last group that the user is removed from, the user is effectively removed from the roster.
	 * 
	 * @param friendId Yahoo IDof the contact to remove from a group.
	 * @param groupId Group to remove the contact from.
	 * @throws IllegalArgumentException if one of the arguments is null or an empty String.
	 */
	void removeFromGroup(Contact contact, String groupId);

	void addToGroup(Contact contact, String groupId) throws IllegalArgumentException;

}
