package org.openymsg.contact.group;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.YahooContactGroup;
import org.openymsg.connection.write.PacketWriter;
import org.openymsg.util.CollectionUtils;

public class SessionGroupImpl implements SessionGroup, SessionGroupCallback {
	private PacketWriter connection;
	private String username;
	private Set<YahooContactGroup> contactGroups = Collections.synchronizedSet(new HashSet<YahooContactGroup>());

	public SessionGroupImpl(PacketWriter connection, String username) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection may not be null");
		}
		if (username == null) {
			throw new IllegalArgumentException("username may not be null");
		}
		this.connection = connection;
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
		this.connection.execute(new ContactGroupRenameMessage(username, group, newName));
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
