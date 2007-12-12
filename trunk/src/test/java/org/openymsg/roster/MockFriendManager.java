package org.openymsg.roster;

import java.io.IOException;

import org.openymsg.network.FriendManager;

/**
 * Mock (empty) implementation of the FriendManager interface. Useful for unit
 * testing.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class MockFriendManager implements FriendManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openymsg.network.FriendManager#removeFriendFromGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public void removeFriendFromGroup(String friendId, String groupId)
			throws IOException {
		// intentionally left empty.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openymsg.network.FriendManager#sendNewFriendRequest(java.lang.String,
	 *      java.lang.String)
	 */
	public void sendNewFriendRequest(String userId, String groupId)
			throws IOException {
		// intentionally left empty.
	}
}
