package org.openymsg;

import java.util.Set;

/**
 * Group of Contacts.  Contacts can be in more that one group.
 * @author neilhart
 *
 */
public interface ContactGroup {

	/**
	 * Get the collection of Contacts.  This is an unmodifiable copy so it has a thread-safe iterator.
	 * @return collection of Contacts for the group.
	 */
	Set<Contact> getContacts();

	/**
	 * Name of the group.  This is unique for the Session.
	 * @return group name
	 */
	String getName();

}
