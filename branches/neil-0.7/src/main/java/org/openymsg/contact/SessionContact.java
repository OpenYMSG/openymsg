package org.openymsg.contact;

import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;

public interface SessionContact {

	Set<Contact> getContacts();
	
	Set<ContactGroup> getContactGroups();
	
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
	void removeFromGroup(Contact contact, ContactGroup group);

	void addToGroup(Contact contact, ContactGroup group) throws IllegalArgumentException;

}
