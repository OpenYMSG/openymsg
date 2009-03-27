package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.AbstractRoster;
import org.openymsg.v1.network.YahooUserV1;

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
public class RosterV1 extends AbstractRoster<YahooUserV1> {

	public RosterV1(final FriendManager manager) {
		super(manager);
	}

}
