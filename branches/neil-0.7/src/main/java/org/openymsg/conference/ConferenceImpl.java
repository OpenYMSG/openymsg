package org.openymsg.conference;

import java.util.Set;

import org.openymsg.Conference;
import org.openymsg.Contact;
import org.openymsg.util.CollectionUtils;

/**
 * Internal representation of a Conference.  Used to change state.
 * @author neilhart
 *
 */
public class ConferenceImpl implements Conference {
	/** Unique id */
	private String id;
	/** set of Contacts that are currently members */
	private Set<Contact> members;

	/**
	 * Create a Conference with the unique id
	 * @param id unique id
	 */
	public ConferenceImpl(String id) {
		this.id = id;
	}

	/**
	 * Get the unique id
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Get the Set of Contacts that are currently in the Conference
	 */
	@Override
	public Set<Contact> getMembers() {
		return CollectionUtils.protectedSet(this.members);
	}

}
