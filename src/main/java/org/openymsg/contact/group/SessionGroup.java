package org.openymsg.contact.group;

import org.openymsg.YahooContactGroup;

import java.util.Set;

public interface SessionGroup {
	/**
	 * get the groups
	 * @return all contact groups
	 */
	Set<YahooContactGroup> getContactGroups();

	/**
	 * Add a group to the messenger. Any group without a contact is only local and will not be saved in Yahoo.
	 * @param groupName new group's name
	 * @throws IllegalArgumentException if null or matches an existing group
	 */
	void addGroup(String groupName) throws IllegalArgumentException;

	/**
	 * Rename an existing group
	 * @param group existing group
	 * @param newName new name
	 * @throws IllegalArgumentException if either paramters are null, if group doesn't exist, if name matches an
	 *         existing group
	 */
	void renameGroup(YahooContactGroup group, String newName) throws IllegalArgumentException;
}
