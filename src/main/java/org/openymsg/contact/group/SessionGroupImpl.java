package org.openymsg.contact.group;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.ContactGroup;
import org.openymsg.execute.Executor;
import org.openymsg.util.CollectionUtils;

public class SessionGroupImpl implements SessionGroup, SessionGroupCallback {
	private Executor executor;
	private String username;
	private Set<ContactGroup> contactGroups = Collections.synchronizedSet(new HashSet<ContactGroup>());

	public SessionGroupImpl(Executor executor, String username) {
		this.executor = executor;
		this.username = username;
	}

	public void renameGroup(ContactGroup group, String newName) throws IllegalStateException, IOException {
		// checkStatus();
		this.executor.execute(new ContactGroupRenameMessage(username, group, newName));
		// transmitGroupRename(group, newName);
	}

	@Override
	public Set<ContactGroup> getContactGroups() {
		return CollectionUtils.protectedSet(this.contactGroups);
	}

	@Override
	public void addedGroups(Set<ContactGroup> contactGroups) {
		this.contactGroups.addAll(contactGroups);
	}

	public boolean possibleAddGroup(ContactGroupImpl group) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addGroup(String groupName) {
		// TODO Auto-generated method stub

	}

}
