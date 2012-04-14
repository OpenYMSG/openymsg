package org.openymsg.contact;

import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.ContactStatus;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

//TODO verify, no status without a contact
public class SessionContactImpl implements SessionContact {
	private SessionRosterImpl sessionRoster;
	private SessionGroupImpl sessionGroup;
	private SessionStatusImpl sessionStatus;
	private Executor executor;

	public SessionContactImpl(Executor executor, String username, SessionContactCallback callback) {
		this.executor = executor;
		sessionRoster = new SessionRosterImpl(executor, username, callback);
		sessionGroup = new SessionGroupImpl(executor, username);
		sessionStatus = new SessionStatusImpl(executor, callback);
		this.executor.register(ServiceType.LIST_15, new ListOfContactsResponse(sessionRoster, sessionGroup,
				sessionStatus));
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
