package org.openymsg.contact.roster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.contact.group.ContactGroupRenameMessage;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;
import org.openymsg.util.CollectionUtils;

public class SessionRosterImpl implements SessionRoster, SessionRosterCallback {
	private static final Log log = LogFactory.getLog(SessionRosterImpl.class);
	private Executor executor;
	private String username;
	private Set<YahooContact> contacts = new HashSet<YahooContact>();
	private SessionRosterCallback callback;
	private boolean rosterLoaded = false;

	public SessionRosterImpl(Executor executor, String username, SessionRosterCallback callback) {
		this.executor = executor;
		this.username = username;
		this.callback = callback;
		this.executor.register(ServiceType.ADD_BUDDY, new ContactAddResponse(this));
	}

	public void renameGroup(YahooContactGroup group, String newName) throws IllegalStateException, IOException {
		// checkStatus();
		this.executor.execute(new ContactGroupRenameMessage(username, group, newName));
		// transmitGroupRename(group, newName);
	}

	@Override
	public void addContact(YahooContact contact, YahooContactGroup group, String message)
			throws IllegalArgumentException {
		// log.trace("Adding new user: " + userId + ", group: " + groupId + ", protocol: " + yahooProtocol);
		// TODO: perhaps we should check the roster to make sure that this
		// friend does not already exist.
		// TODO validate group
		if (contact == null) {
			throw new IllegalArgumentException("Argument 'contact' cannot be null");
		}
		if (group == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null");
		}
		if (this.contacts.contains(contact)) {
			throw new IllegalArgumentException("Contact already exists");
		}
		// TODO handle name
		this.executor.execute(new ContactAddMessage(this.username, contact, group, message, new Name(null, null)));
	}

	@Override
	public void removeFromGroup(YahooContact contact, YahooContactGroup group) {
		if (contact == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null.");
		}
		if (group == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null.");
		}
		// if (!this.sessionGroup.getContactGroups().contains(group)) {
		// throw new IllegalArgumentException("Not an existing group");
		// }
		if (!group.getContacts().contains(contact)) {
			throw new IllegalArgumentException("Contact not in group");
		}
		this.executor.execute(new ContactRemoveMessage(this.username, contact, group));
	}

	@Override
	public void acceptFriendAuthorization(YahooContact contact) throws IllegalStateException {
		this.executor.execute(new ContactAddAcceptMessage(this.username, contact));
	}

	@Override
	public void rejectFriendAuthorization(YahooContact contact, String message) throws IllegalStateException {
		this.executor.execute(new ContactAddDeclineMessage(this.username, contact, message));
	}

	// TODO - this look cool
	// public void refreshFriends() throws IllegalStateException, IOException {
	// checkStatus();
	// transmitList();
	// }

	public void loadedContact(YahooContact contact) {
		log.trace("loadedContact: " + contact);
		if (this.rosterLoaded) {
			log.warn("Loading contact after roster is loaded: " + contact);
		}
		this.contacts.add(contact);
		this.callback.addedContact(contact);
	}

	@Override
	public void addedContact(YahooContact contact) {
		log.trace("addedContact: " + contact);
		if (!this.rosterLoaded) {
			log.warn("Loading contact before roster is loaded: " + contact);
		}
		this.contacts.add(contact);
		this.callback.addedContact(contact);
		// for (Contact contact : usersOnFriendsList) {
		// System.err.println("add: " + contactImpl.getId() + "/" + contactImpl.getProtocol() + "/" +
		// contactImpl.getGroupIds());
		// }
	}

	@Override
	public Set<YahooContact> getContacts() {
		return CollectionUtils.protectedSet(this.contacts);
	}

	// TODO is this only for ack? should check with timeout
	public boolean possibleAddContact(YahooContact contact) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void receivedContactAddFailure(YahooContact contact, ContactAddFailure failure, String friendAddStatus) {
		this.callback.receivedContactAddFailure(contact, failure, friendAddStatus);
	}

	@Override
	public void receivedContactAddAccepted(YahooContact contact) {
		this.callback.receivedContactAddAccepted(contact);
	}

	@Override
	public void receivedContactAddDeclined(YahooContact contact, String message) {
		this.callback.receivedContactAddDeclined(contact, message);
	}

	@Override
	public void receivedContactAddRequest(YahooContact contact, Name name, String message) {
		this.callback.receivedContactAddRequest(contact, name, message);
	}

	@Override
	public void removedContact(YahooContact contact) {
		this.callback.removedContact(contact);
	}

	@Override
	public void rosterLoaded() {
		log.trace("rosterLoaded");
		this.rosterLoaded = true;
		this.callback.rosterLoaded();
	}

	public void contactAddFailure(YahooContact contact, ContactAddFailure failure) {
		// TODO Auto-generated method stub

	}

	public boolean possibleRemoveContact(YahooContact contact) {
		// TODO Auto-generated method stub
		return false;
	}

}
