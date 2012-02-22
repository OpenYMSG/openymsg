package org.openymsg.group;

import java.util.Set;

import org.openymsg.ContactGroup;

public interface SessionGroupCallback {
	void addedGroups(Set<ContactGroup> contactGroups);
	

}
