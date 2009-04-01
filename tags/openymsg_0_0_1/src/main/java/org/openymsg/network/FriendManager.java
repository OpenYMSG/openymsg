package org.openymsg.network;

import java.io.IOException;

import org.openymsg.roster.Roster;

/**
 * A FriendManager is used to create or remove Friends on a contact list. The
 * {@link Roster} class makes use of it to translate user interaction into Yahoo
 * events that {@link Session} sends to the network.
 * <p>
 * Although using FriendManager interfaces directly in your code, most OpenYMSG
 * library users will find the {@link Roster} class more intuative to use.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public interface FriendManager {

	/**
	 * Sends a new request to become friends to the user. This is a subscription
	 * request, to which to other user should reply to. Responses will arrive
	 * asynchronously.
	 * 
	 * @param userId
	 *            Yahoo id of user to add as a new friend.
	 * @param groupId
	 *            Name of the group to add the new friend to.
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null or an empty String.
	 * @throws IllegalStateException
	 *             if this session is not logged onto the Yahoo network
	 *             correctly.
	 * @throws IOException
	 *             if any problem occured related to creating or sending the
	 *             request to the Yahoo network.
	 */
	public void sendNewFriendRequest(final String userId, final String groupId)
			throws IOException;

	/**
	 * Instructs the Yahoo network to remove this friend from the particular
	 * group on the roster of the current user. If this is the last group that
	 * the user is removed from, the user is effectively removed from the
	 * roster.
	 * 
	 * @param friendId
	 *            Yahoo IDof the contact to remove from a group.
	 * @param groupId
	 *            Group to remove the contact from.
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null or an empty String.
	 * @throws IllegalStateException
	 *             if this session is not logged onto the Yahoo network
	 *             correctly.
	 * @throws IOException
	 *             on any problem while trying to create or send the packet.
	 */
	public void removeFriendFromGroup(final String friendId,
			final String groupId) throws IOException;

}
