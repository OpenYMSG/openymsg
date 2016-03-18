package org.openymsg.contact.group;

import org.openymsg.YahooContactGroup;
import org.openymsg.connection.YahooConnection;
import org.openymsg.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SessionGroupImpl implements SessionGroup, SessionGroupCallback {
	private YahooConnection executor;
	private String username;
	private Set<YahooContactGroup> contactGroups = Collections.synchronizedSet(new HashSet<YahooContactGroup>());

	public SessionGroupImpl(YahooConnection executor, String username) {
		if (executor == null) {
			throw new IllegalArgumentException("Executor may not be null");
		}
		if (username == null) {
			throw new IllegalArgumentException("username may not be null");
		}
		this.executor = executor;
		this.username = username;
	}

	@Override
	public void renameGroup(YahooContactGroup group, String newName) throws IllegalArgumentException {
		if (group == null) {
			throw new IllegalArgumentException("Group may not be null");
		}
		if (newName == null) {
			throw new IllegalArgumentException("newName may not be null");
		}
		for (YahooContactGroup existingGroup : this.contactGroups) {
			if (existingGroup.getName().equalsIgnoreCase(newName)) {
				throw new IllegalArgumentException("newName matches a group with name: " + existingGroup.getName());
			}
		}
		if (!this.contactGroups.contains(group)) {
			throw new IllegalArgumentException("group is not in the existing group list");
		}
		this.executor.execute(new ContactGroupRenameMessage(username, group, newName));
	}

	@Override
	public Set<YahooContactGroup> getContactGroups() {
		return CollectionUtils.protectedSet(this.contactGroups);
	}

	@Override
	public void addedGroups(Set<YahooContactGroup> contactGroups) {
		this.contactGroups.addAll(contactGroups);
	}

	public boolean possibleAddGroup(ContactGroupImpl group) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addGroup(String groupName) throws IllegalArgumentException {
		// TODO Auto-generated method stub
	}

	public boolean possibleRemoveGroup(ContactGroupImpl group) {
		// TODO Auto-generated method stub
		return false;
	}
}
