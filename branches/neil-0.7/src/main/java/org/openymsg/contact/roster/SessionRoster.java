package org.openymsg.contact.roster;

import java.util.Set;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;

public interface SessionRoster {

	Set<YahooContact> getContacts();

	void acceptFriendAuthorization(YahooContact contact) throws IllegalStateException;

	void rejectFriendAuthorization(YahooContact contact, String message) throws IllegalStateException;

	/**
	 * Instructs the Yahoo network to remove this friend from the particular group on the roster of the current user. If
	 * this is the last group that the user is removed from, the user is effectively removed from the roster.
	 * @category Contact
	 * @param friendId Yahoo IDof the contact to remove from a group.
	 * @param groupId Group to remove the contact from.
	 * @throws IllegalArgumentException if one of the arguments is null or an empty String.
	 */
	void removeFromGroup(YahooContact contact, YahooContactGroup group);

	/**
	 * Add a new Contact. The contact will be added to the ContactGroup. This is not for adding an existing contact to
	 * another group Success will be calling SessionContactCallback.addedContact(Contact). Failure will be calling
	 * SessionContactCallback.contactAddFailure(Contact, ContactAddFailure, String)
	 * @param contact new contact
	 * @param group existing group
	 * @throws IllegalArgumentException if either contact or group is null or contact already exists
	 */
	// TODO what to do for new group
	void addContact(YahooContact contact, YahooContactGroup group) throws IllegalArgumentException;

	// void moveToGroup(Contact contact, ContactGroup group) throws IllegalArgumentException;
}
