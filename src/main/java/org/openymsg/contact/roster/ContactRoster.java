package org.openymsg.contact.roster;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.util.CollectionUtils;

/**
 * Roster of contacts. This keeps a list of contacts given all of the different actions from both the user and yahoo.
 * There are two internal lists, the yahoo list and the add request list. These are used to check state and log any
 * possible problems.
 * @author neilhart
 */
public class ContactRoster {
	/** Logger */
	private static final Log log = LogFactory.getLog(ContactRoster.class);
	/** Simple set of contacts that match what yahoo has */
	private Set<YahooContact> contacts = new HashSet<YahooContact>();
	/** Requests that where sent, but not ack'ed */
	private Set<YahooContact> outstandingAddRequests = new HashSet<YahooContact>();

	/**
	 * contact is list from yahoo
	 * @param contact contact to check
	 * @return true is contact is in yahoo list
	 */
	public boolean contains(YahooContact contact) {
		return contacts.contains(contact);
	}

	/**
	 * Contact was received during initial login process
	 * @param contact
	 */
	public synchronized void loadedContact(YahooContact contact) {
		this.contacts.add(contact);
	}

	/**
	 * Add request submitted, contact will be check against internal lists
	 * @param contact add request checked
	 */
	public synchronized void addRequestSubmitted(YahooContact contact) {
		if (outstandingAddRequests.contains(contact)) {
			log.warn("addRequest resubmitted for contact: " + contact);
		}
		if (contacts.contains(contact)) {
			log.warn("addRequest submitted for contact in list: " + contact);
		}
		outstandingAddRequests.add(contact);
	}

	/**
	 * Add request ack'ed, contact is moved from outstanding to yahoo list
	 * @param contact new yahoo contact
	 */
	public synchronized void addRequestAcked(YahooContact contact) {
		if (!outstandingAddRequests.remove(contact)) {
			log.warn("addRequest ack for contact not in oustanding request: " + contact);
		}
		contacts.add(contact);
	}

	/**
	 * Add request declined, contact is removed from yahoo list
	 * @param contact deleted contact
	 */
	public synchronized void addRequestDeclined(YahooContact contact) {
		if (outstandingAddRequests.remove(contact)) {
			log.warn("addRequest decline for contact still in outstanding request: " + contact);
		}
		if (!contacts.remove(contact)) {
			log.warn("addRequest decline for contact not in list: " + contact);
		}
	}

	/**
	 * Add request accepted, contact is kept in yahoo list
	 * @param contact
	 */
	public synchronized void addRequestAccepted(YahooContact contact) {
		if (outstandingAddRequests.remove(contact)) {
			log.warn("addRequest accepted for contact still in outstanding request: " + contact);
		}
		if (!contacts.contains(contact)) {
			log.warn("addRequest accepted for contact not in list: " + contact);
			contacts.add(contact);
		}
	}

	/**
	 * Get the list of yahoo contacts
	 * @return list of yahoo contacts
	 */
	public synchronized Set<YahooContact> getContacts() {
		return CollectionUtils.protectedSet(this.contacts);
	}

}
