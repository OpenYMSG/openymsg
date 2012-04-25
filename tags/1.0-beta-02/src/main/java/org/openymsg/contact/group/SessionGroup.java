package org.openymsg.contact.group;

import java.util.Set;

import org.openymsg.YahooContactGroup;

public interface SessionGroup {
	/**
	 * get the groups
	 * @category Group
	 * @return all contact groups
	 */
	Set<YahooContactGroup> getContactGroups();

	void addGroup(String groupName);
}
