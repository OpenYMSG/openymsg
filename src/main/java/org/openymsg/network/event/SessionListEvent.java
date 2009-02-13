package org.openymsg.network.event;

import java.util.Set;

import org.openymsg.network.ContactListType;
import org.openymsg.network.YahooUser;

/**
 * Event that is triggered right after the session receives an entire list of
 * contacts from the server. This typically occurs right after authentication.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public interface SessionListEvent<T extends YahooUser> extends SessionEvent {


	/**
	 * Returns the type of the list.
	 * 
	 * @return Type of the contact list that was received.
	 */
	ContactListType getType();

	/**
	 * Returns an unmodifiable set of the contacts on this list.
	 * 
	 * @return The users that make up the (complete) list.
	 */
	Set<T> getContacts();

}
