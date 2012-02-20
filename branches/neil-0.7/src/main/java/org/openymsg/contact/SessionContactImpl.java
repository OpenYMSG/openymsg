package org.openymsg.contact;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionContactImpl implements SessionContact, ContactListener {
	private static final Log log = LogFactory.getLog(SessionContactImpl.class);
	private Executor executor;
	private Set<ContactImpl> contacts = new HashSet<ContactImpl>();

	public SessionContactImpl(Executor executor) {
		this.executor = executor;
		this.executor.register(ServiceType.LIST_15, new ListOfContactsResponse(this));
	}

	@Override
	public void addContacts(Set<ContactImpl> usersOnFriendsList) {
		this.contacts.addAll(usersOnFriendsList);
		for (ContactImpl contactImpl : usersOnFriendsList) {
//			System.err.println("add: " + contactImpl.getId() + "/" + contactImpl.getProtocol() + "/" + contactImpl.getGroupIds());
		}
	}

	@Override
	public void addIgnoredFriends(Set<ContactImpl> usersOnIgnoreList) {
		for (ContactImpl contactImpl : usersOnIgnoreList) {
			System.err.println("ignored: " + contactImpl);
		}
	}

	@Override
	public void addPendingFriends(Set<ContactImpl> usersOnPendingList) {
		for (ContactImpl contactImpl : usersOnPendingList) {
//			System.err.println("pending:" + contactImpl.getId() + "/" + contactImpl.getProtocol());
		}
	}

	/**
	 * ingores case
	 * @param userId
	 * @return
	 */
	public ContactImpl getUser(String userId) {
		for (ContactImpl contact : this.contacts) {
			if (contact.getId().equalsIgnoreCase(userId)) {
				return contact;
			}
		}
		return null;
	}

}
