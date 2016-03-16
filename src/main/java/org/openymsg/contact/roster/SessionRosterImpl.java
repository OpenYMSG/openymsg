package org.openymsg.contact.roster;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;

public class SessionRosterImpl implements SessionRoster, SessionRosterCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionRosterImpl.class);
	private YahooConnection executor;
	private String username;
	private ContactRoster contacts = new ContactRoster();
	private SessionRosterCallback callback;
	private boolean rosterLoaded = false;

	public SessionRosterImpl(YahooConnection executor, String username, SessionRosterCallback callback) {
		this.executor = executor;
		this.username = username;
		this.callback = callback;
		this.executor.register(ServiceType.Y7_BUDDY_AUTHORIZATION, new ContactAddAuthorizationResponse(this));
	}

	@Override
	public void addContact(YahooContact contact, YahooContactGroup group, String message)
			throws IllegalArgumentException {
		// log.trace("Adding new user: " + userId + ", group: " + groupId + ", protocol: " + yahooProtocol);
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
		this.executor.execute(new ContactAddMessage(this.username, contact, group, message, null));
	}

	@Override
	public void removeFromGroup(YahooContact contact, YahooContactGroup group) {
		if (contact == null) {
			throw new IllegalArgumentException("Argument 'contact' cannot be null.");
		}
		if (group == null) {
			throw new IllegalArgumentException("Argument 'group' cannot be null.");
		}
		if (!group.getContacts().contains(contact)) {
			throw new IllegalArgumentException("Contact not in group");
		}
		this.executor.execute(new ContactRemoveMessage(this.username, contact, group));
	}

	@Override
	public void acceptFriendAuthorization(String id, YahooContact contact) throws IllegalStateException {
		this.executor.execute(new ContactAddAcceptMessage(id, contact));
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
		this.contacts.loadedContact(contact);
		this.callback.addedContact(contact);
	}

	@Override
	public Set<YahooContact> getContacts() {
		return contacts.getContacts();
	}

	// TODO is this only for ack? should check with timeout
	public void receivedContactAddAck(YahooContact contact) {
		contacts.addRequestAcked(contact);
		callback.addedContact(contact);
	}

	@Override
	public void receivedContactAddFailure(YahooContact contact, ContactAddFailure failure, String friendAddStatus) {
		this.callback.receivedContactAddFailure(contact, failure, friendAddStatus);
	}

	@Override
	public void receivedContactAddAccepted(YahooContact contact) {
		this.contacts.addRequestAccepted(contact);
		this.callback.receivedContactAddAccepted(contact);
	}

	@Override
	public void receivedContactAddDeclined(YahooContact contact, String message) {
		this.contacts.addRequestDeclined(contact);
		this.callback.receivedContactAddDeclined(contact, message);
	}

	@Override
	public void receivedContactAddRequest(String id, YahooContact contact, Name name, String message) {
		this.callback.receivedContactAddRequest(id, contact, name, message);
	}

	@Override
	// TODO is why is there here
	public void removedContact(YahooContact contact) {
		this.callback.removedContact(contact);
	}

	@Override
	public void rosterLoaded() {
		log.trace("rosterLoaded");
		this.rosterLoaded = true;
		this.callback.rosterLoaded();
	}

	public boolean possibleRemoveContact(YahooContact contact) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	// NOT sure I need this
	public void addedContact(YahooContact contact) {
		log.trace("addedContact: " + contact);
		if (!this.rosterLoaded) {
			log.warn("Loading contact before roster is loaded: " + contact);
		}
		this.callback.addedContact(contact);
		// for (Contact contact : usersOnFriendsList) {
		// System.err.println("add: " + contactImpl.getId() + "/" + contactImpl.getProtocol() + "/" +
		// contactImpl.getGroupIds());
		// }
	}

}
