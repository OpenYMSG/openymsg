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
	
	/**
	 * Is the group still active.  False if it has been deleted.  A deleted group has no contacts.
	 * If a group is created with the same name, it will be a new instance.  A group will not become active
	 * after it is deleted
	 * @return false if deleted
	 */
	boolean isActive();

}
