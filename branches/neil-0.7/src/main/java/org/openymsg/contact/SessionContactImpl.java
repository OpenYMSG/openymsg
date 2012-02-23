package org.openymsg.contact;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.execute.Executor;
import org.openymsg.group.ContactGroupRenameMessage;
import org.openymsg.group.SessionGroup;
import org.openymsg.group.SessionGroupImpl;
import org.openymsg.network.ServiceType;
import org.openymsg.status.SessionStatus;
import org.openymsg.status.SessionStatusImpl;
import org.openymsg.util.CollectionUtils;

public class SessionContactImpl implements SessionContact, SessionContactCallback {
	private Executor executor;
	private String username;
	private Set<Contact> contacts = Collections.synchronizedSet(new HashSet<Contact>());
	private SessionGroupImpl sessionGroup;
	private SessionStatusImpl sessionStatus;
	
	public SessionContactImpl(Executor executor, String username) {
		this.executor = executor;
		this.username = username;
	}

	public void initialize() throws IllegalStateException {
		if (this.sessionGroup == null) {
			throw new IllegalStateException("SessionGroup is not set");
		}
		if (this.sessionStatus == null) {
			throw new IllegalStateException("SessionStatus is not set");
		}
		this.executor.register(ServiceType.LIST_15, new ListOfContactsResponse(this, sessionGroup, sessionStatus));
	}
    public void renameGroup(ContactGroup group, String newName) throws IllegalStateException, IOException {
//        checkStatus();
    	this.executor.execute(new ContactGroupRenameMessage(username, group, newName));
//        transmitGroupRename(group, newName);
    }

	@Override
	public void addContact(Contact contact, ContactGroup group) throws IllegalArgumentException {
		// log.trace("Adding new user: " + userId + ", group: " + groupId + ", protocol: " + yahooProtocol);
		// TODO: perhaps we should check the roster to make sure that this
		// friend does not already exist.
		//TODO validate group
		//TODO check if user already in group
		if (contact == null) {
			throw new IllegalArgumentException("Argument 'contact' cannot be null");
		}
		if (group == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null");
		}
		if (this.contacts.contains(contact)) {
			throw new IllegalArgumentException("Contact already exists");
		}
		this.executor.execute(new ContactAddRequestMessage(this.username, contact, group));
	}

	@Override
	public void removeFromGroup(Contact contact, ContactGroup group) {
		if (contact == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null.");
		}
		if (group == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null.");
		}
		if (!this.sessionGroup.getContactGroups().contains(group)) {
			throw new IllegalArgumentException("Not an existing group");
		}
		if (!group.getContacts().contains(contact)) {
			throw new IllegalArgumentException("Contact not in group");
		}
		this.executor.execute(new ContactRemoveRequestMessage(this.username, contact, group));
	}

	@Override
	public void acceptFriendAuthorization(Contact contact) throws IllegalStateException {
		// checkStatus();
		this.executor.execute(new ContactAddRequestAccept(this.username, contact));
	}

	@Override
	public void rejectFriendAuthorization(Contact contact, String message) throws IllegalStateException {
		// checkStatus();
		this.executor.execute(new ContactAddRequestDecline(this.username, contact, message));
	}

	// TODO - this look cool
	// public void refreshFriends() throws IllegalStateException, IOException {
	// checkStatus();
	// transmitList();
	// }

	@Override
	public void addedContacts(Set<Contact> usersOnFriendsList) {
		this.contacts.addAll(usersOnFriendsList);
//		for (Contact contact : usersOnFriendsList) {
			// System.err.println("add: " + contactImpl.getId() + "/" + contactImpl.getProtocol() + "/" +
			// contactImpl.getGroupIds());
//		}
	}

	@Override
	public Set<Contact> getContacts() {
		return CollectionUtils.protectedSet(this.contacts);
	}

	public boolean possibleContact(Contact contact) {
		// TODO Auto-generated method stub
		return false;
	}

	public void contactAddFailure(Contact contact, ContactAddFailure failure) {
		// TODO Auto-generated method stub
		
	}

	public void contactAddFailure(Contact contact, ContactAddFailure failure, String friendAddStatus) {
		// TODO Auto-generated method stub
		
	}

	public void setGroupSession(SessionGroupImpl sessionGroup) {
		this.sessionGroup = sessionGroup;
	}

	public void setStatusSession(SessionStatusImpl sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

}
