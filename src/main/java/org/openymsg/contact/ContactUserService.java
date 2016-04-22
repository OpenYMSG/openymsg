package org.openymsg.contact;

import java.util.Set;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooContactStatus;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.ContactStatusUserService;

// TODO verify, no status without a contact
public class ContactUserService implements SessionContact {
	private final SessionRosterImpl sessionRoster;
	private final SessionGroupImpl sessionGroup;
	private final ContactStatusUserService sessionStatus;

	public ContactUserService(SessionRosterImpl sessionRoster, SessionGroupImpl sessionGroup,
			ContactStatusUserService sessionStatus) {
		this.sessionRoster = sessionRoster;
		this.sessionGroup = sessionGroup;
		this.sessionStatus = sessionStatus;
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

}
