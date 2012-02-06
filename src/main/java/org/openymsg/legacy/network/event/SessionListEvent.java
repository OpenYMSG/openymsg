package org.openymsg.legacy.network.event;

import java.util.Collections;
import java.util.Set;

import org.openymsg.legacy.network.ContactListType;
import org.openymsg.legacy.network.YahooUser;

/**
 * Event that is triggered right after the session receives an entire list of contacts from the server. This typically
 * occurs right after authentication.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class SessionListEvent extends SessionEvent {

    private static final long serialVersionUID = 8418335191110496114L;

    private final ContactListType type;

    private final Set<YahooUser> contacts;

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
    public SessionListEvent(Object source, ContactListType type, Set<YahooUser> contacts) {
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
    public Set<YahooUser> getContacts() {
        return contacts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openymsg.legacy.network.event.SessionEvent#toString()
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append(" list type:").append(type).append(" size:").append(contacts.size());
        return sb.toString();
    }
}
