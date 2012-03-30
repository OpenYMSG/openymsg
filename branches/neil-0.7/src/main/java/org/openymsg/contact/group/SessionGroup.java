package org.openymsg.contact.group;

import java.util.Set;

import org.openymsg.ContactGroup;

public interface SessionGroup {
	/**
	 * get the groups
	 * @category Group
	 * @return all contact groups
	 */
	Set<ContactGroup> getContactGroups();

	void addGroup(String groupName);
}
