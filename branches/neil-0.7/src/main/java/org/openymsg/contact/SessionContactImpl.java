package org.openymsg.contact;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionContactImpl implements SessionContact, SessionContactCallback {
	private Executor executor;
	private Set<Contact> contacts = new HashSet<Contact>();
	private String username;

	public SessionContactImpl(Executor executor, String username) {
		this.executor = executor;
		this.username = username;
		this.executor.register(ServiceType.LIST_15, new ListOfContactsResponse(this));
	}

    public void renameGroup(ContactGroup group, String newName) throws IllegalStateException, IOException {
//        checkStatus();
    	this.executor.execute(new ContactGroupRenameMessage(username, group, newName));
//        transmitGroupRename(group, newName);
    }


	/**
	 * Sends a new request to become friends to the user. This is a subscription request, to which to other user should
	 * reply to. Responses will arrive asynchronously.
	 * 
	 * @param userId Yahoo id of user to add as a new friend.
	 * @param groupId Name of the group to add the new friend to.
	 * @throws IllegalArgumentException if one of the arguments is null or an empty String.
	 * @throws IllegalStateException if this session is not logged onto the Yahoo network correctly.
	 * @throws IOException if any problem occured related to creating or sending the request to the Yahoo network.
	 */
	@Override
	public void addToGroup(Contact contact, String groupId) throws IllegalArgumentException {
		// log.trace("Adding new user: " + userId + ", group: " + groupId + ", protocol: " + yahooProtocol);
		// TODO: perhaps we should check the roster to make sure that this
		// friend does not already exist.
		if (groupId == null || groupId.length() == 0) {
			throw new IllegalArgumentException("Argument 'groupId' cannot be null or an empty String.");
		}
		this.executor.execute(new ContactAddRequestMessage(this.username, contact, groupId));
		// transmitFriendAdd(userId, groupId, yahooProtocol);
	}

	/**
	 * Instructs the Yahoo network to remove this friend from the particular group on the roster of the current user. If
	 * this is the last group that the user is removed from, the user is effectively removed from the roster.
	 * 
	 * @param friendId Yahoo IDof the contact to remove from a group.
	 * @param groupId Group to remove the contact from.
	 * @throws IllegalArgumentException if one of the arguments is null or an empty String.
	 * @throws IllegalStateException if this session is not logged onto the Yahoo network correctly.
	 * @throws IOException on any problem while trying to create or send the packet.
	 */
	@Override
	public void removeFromGroup(Contact contact, String groupId) {
		if (groupId == null || groupId.length() == 0) {
			throw new IllegalArgumentException("Argument 'groupId' cannot be null or an empty String.");
		}
		this.executor.execute(new ContactRemoveRequestMessage(this.username, contact, groupId));
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
	public void addContacts(Set<Contact> usersOnFriendsList) {
		this.contacts.addAll(usersOnFriendsList);
//		for (Contact contact : usersOnFriendsList) {
			// System.err.println("add: " + contactImpl.getId() + "/" + contactImpl.getProtocol() + "/" +
			// contactImpl.getGroupIds());
//		}
	}

	@Override
	public void addIgnored(Set<Contact> usersOnIgnoreList) {
		for (Contact contact : usersOnIgnoreList) {
			System.err.println("ignored: " + contact);
		}
	}

	@Override
	public void addPending(Set<Contact> usersOnPendingList) {
//		for (Contact contact : usersOnPendingList) {
			// System.err.println("pending:" + contactImpl.getId() + "/" + contactImpl.getProtocol());
//		}
	}
}
