package org.openymsg.roster;

import java.util.Set;

import org.openymsg.network.YahooUser;
import org.openymsg.network.event.SessionListener;

/**
 * A Roster object is a representation of the contact list of a particular user.
 * The Roster is a set of all users to which the owner of the Roster has
 * subscribed.
 * <p>
 * Listeners can be registered to a Roster instance. After the listener has been
 * registered, it will receive events that reflect changes to the Roster.
 * <p>
 * The Roster class implements the Set interface, as it represents a unique set
 * of Yahoo Users. To avoid accidental mass subscription or unsubscription, most
 * bulk operations (addAll, removeAll) are unsupported in this implementation.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public interface Roster<T extends YahooUser> extends Set<T>, SessionListener {


	/**
	 * Adds a listener that will be notified of any roster changes. This
	 * operation is thread safe.
	 * 
	 * @param listener
	 *            The new listener that gets notified of any roster changes.
	 */
	void addRosterListener(final RosterListener listener);
	
	/**
	 * Removes the listener from the set of listeners that will be notified of
	 * changes to this roster. This operation is thread safe.
	 * 
	 * @param listener
	 *            The listener that should be removed.
	 */
	void removeRosterListener(final RosterListener listener);
				
	/**
	 * Checks if this roster contains a user that is identified by the provided
	 * ID.
	 * @param userId
	 *            ID of a user whose presence on this roster is to be tested.
	 * @return <tt>true</tt> if this set contains the specified element.
	 * @throws NullPointerException
	 *             if the specified userId is null.
	 * @throws IllegalArgumentException
	 *             if the specified userId is an empty String.
	 */
	boolean containsUser(String userId);
	
	
	/**
	 * Returns the user specified by the provided ID, or null if no such user
	 * exists on the roster.
	 * 
	 * @param userId
	 *            the ID of the user to return.
	 * @return the User matched by the ID, or null if no such user exists on
	 *         this roster.
	 */
	T getUser(final String userId);
	
}
