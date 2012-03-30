package org.openymsg.contact;

import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.ContactStatus;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.execute.Executor;

public class SessionContactImpl implements SessionContact {
	private SessionRosterImpl sessionRoster;
	private SessionGroupImpl sessionGroup;
	@SuppressWarnings("unused")
	private SessionStatusImpl sessionStatus;

	public SessionContactImpl(Executor executor, String username) {
		sessionRoster = new SessionRosterImpl(executor, username);
		sessionGroup = new SessionGroupImpl(executor, username);
		sessionStatus = new SessionStatusImpl(executor);
		this.sessionRoster.setGroupSession(this.sessionGroup);
		this.sessionRoster.setStatusSession(this.sessionStatus);
		this.sessionRoster.initialize();
	}

	@Override
	public Set<ContactGroup> getContactGroups() {
		return this.sessionGroup.getContactGroups();
	}

	@Override
	public Set<Contact> getContacts() {
		return this.sessionRoster.getContacts();
	}

	@Override
	public void acceptFriendAuthorization(Contact contact) throws IllegalStateException {
		this.sessionRoster.acceptFriendAuthorization(contact);
	}

	@Override
	public void rejectFriendAuthorization(Contact contact, String message) throws IllegalStateException {
		this.sessionRoster.rejectFriendAuthorization(contact, message);
	}

	@Override
	public void removeFromGroup(Contact contact, ContactGroup group) {
		this.sessionRoster.removeFromGroup(contact, group);
	}

	@Override
	public void addContact(Contact contact, ContactGroup group) throws IllegalArgumentException {
		this.sessionRoster.addContact(contact, group);
	}

	@Override
	public void addGroup(String groupName) {
		this.sessionGroup.addGroup(groupName);
	}

	@Override
	public ContactStatus getStatus(Contact contact) {
		return this.sessionStatus.getStatus(contact);
	}

}
