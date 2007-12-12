package org.openymsg.roster;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openymsg.network.FriendManager;

/**
 * Mock (empty) implementation of the FriendManager interface. Useful for unit
 * testing.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class MockFriendManager implements FriendManager {

	private static final Logger log = Logger.getLogger(MockFriendManager.class);
	
	private String friendId = null;
	private String groupId = null;
	private String method = null;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openymsg.network.FriendManager#removeFriendFromGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public void removeFriendFromGroup(String friendId, String groupId)
			throws IOException {
		log.info("Mock removeFriendFromGroup triggered with friendId ["+friendId+"] and groupId ["+groupId+"]");
		this.friendId = friendId;
		this.groupId = groupId;
		this.method = "removeFriendFromGroup";
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openymsg.network.FriendManager#sendNewFriendRequest(java.lang.String,
	 *      java.lang.String)
	 */
	public void sendNewFriendRequest(String userId, String groupId)
			throws IOException {
		log.info("Mock sendNewFriendRequest triggered with friendId ["+userId+"] and groupId ["+groupId+"]");
		this.friendId = userId;
		this.groupId = groupId;
		this.method = "sendNewFriendRequest";
	}

	/**
	 * @return the friendId
	 */
	public String getFriendId() {
		return friendId;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
}
