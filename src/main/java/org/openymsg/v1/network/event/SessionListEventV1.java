package org.openymsg.v1.network.event;

import java.util.Collections;
import java.util.Set;

import org.openymsg.network.ContactListType;
import org.openymsg.network.event.DefaultSessionEvent;
import org.openymsg.network.event.SessionListEvent;
import org.openymsg.v1.network.YahooUserV1;

/**
 * Event that is triggered right after the session receives an entire list of
 * contacts from the server. This typically occurs right after authentication.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class SessionListEventV1 extends DefaultSessionEvent implements SessionListEvent<YahooUserV1> {

	private static final long serialVersionUID = 8418335191110496114L;

	private final ContactListType type;

	private final Set<YahooUserV1> contacts;

	/**
	 * Constructs a new instance, based on the provided arguments.
	 * 
	 * @param source
	 *            Logical source of the event.
	 * @param type
	 *            Type of the contact list that was received.
	 * @param contacts
	 *            The users that make up the (complete) list.
	 */
	public SessionListEventV1(Object source, ContactListType type,
			Set<YahooUserV1> contacts) {
		super(source);
		this.type = type;
		this.contacts = Collections.unmodifiableSet(contacts);
	}

	/**
	 * Returns the type of the list.
	 * 
	 * @return Type of the contact list that was received.
	 */
	public ContactListType getType() {
		return type;
	}

	/**
	 * Returns an unmodifiable set of the contacts on this list.
	 * 
	 * @return The users that make up the (complete) list.
	 */
	public Set<YahooUserV1> getContacts() {
		return contacts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openymsg.network.event.SessionEvent#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" list type:").append(type).append(" size:").append(
				contacts.size());
		return sb.toString();
	}
}
