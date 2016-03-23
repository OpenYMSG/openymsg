package org.openymsg.contact;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooContactStatus;
import org.openymsg.connection.YahooConnection;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.ContactStatusImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.network.ServiceType;

import java.util.Set;

// TODO verify, no status without a contact
public class SessionContactImpl implements SessionContact {
	private SessionRosterImpl sessionRoster;
	private SessionGroupImpl sessionGroup;
	private SessionStatusImpl sessionStatus;
	private YahooConnection executor;

	public SessionContactImpl(YahooConnection executor, String username, SessionContactCallback callback) {
		this.executor = executor;
		sessionRoster = new SessionRosterImpl(executor, username, callback);
		sessionGroup = new SessionGroupImpl(executor, username);
		sessionStatus = new SessionStatusImpl(executor, callback);
		this.executor.register(ServiceType.LIST_15,
				new ListOfContactsResponse(sessionRoster, sessionGroup, sessionStatus));
		this.executor.register(ServiceType.REMOVE_BUDDY, new ContactRemoveAckResponse(sessionRoster, sessionGroup));
		this.executor.register(ServiceType.ADD_BUDDY,
				new ContactAddAckResponse(sessionRoster, sessionGroup, sessionStatus));
	}

	@Override
	public Set<YahooContactGroup> getContactGroups() {
		return this.sessionGroup.getContactGroups();
	}

	@Override
	public Set<YahooContact> getContacts() {
		return this.sessionRoster.getContacts();
	}

	@Override
	public void acceptFriendAuthorization(String id, YahooContact contact) throws IllegalStateException {
		this.sessionRoster.acceptFriendAuthorization(id, contact);
	}

	@Override
	public void rejectFriendAuthorization(YahooContact contact, String message) throws IllegalStateException {
		this.sessionRoster.rejectFriendAuthorization(contact, message);
	}

	@Override
	public void removeFromGroup(YahooContact contact, YahooContactGroup group) {
		// if (!this.sessionGroup.getContactGroups().contains(group)) {
		// throw new IllegalArgumentException("Not an existing group");
		// }
		this.sessionRoster.removeFromGroup(contact, group);
	}

	@Override
	public void addContact(YahooContact contact, YahooContactGroup group, String message)
			throws IllegalArgumentException {
		// TODO validate group
		this.sessionRoster.addContact(contact, group, message);
	}

	@Override
	public void addGroup(String groupName) {
		this.sessionGroup.addGroup(groupName);
	}

	@Override
	public YahooContactStatus getStatus(YahooContact contact) {
		return this.sessionStatus.getStatus(contact);
	}

	@Override
	public void renameGroup(YahooContactGroup group, String newName) throws IllegalArgumentException {
		this.sessionGroup.renameGroup(group, newName);
	}

	public void receivedContactLogoff(YahooContact contact) {
		sessionStatus.statusUpdate(contact, ContactStatusImpl.OFFLINE);
	}
}
