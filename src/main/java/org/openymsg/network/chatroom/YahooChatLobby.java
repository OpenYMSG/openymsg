/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.network.chatroom;

import java.util.Collection;

/**
 * Represents a single chat lobby. Yahoo chatrooms consist of one or more
 * numbered lobbies inside each public/private room. The name of room and the
 * number of the lobby (separated by a colon) form the 'network name' of the
 * lobby - used by Yahoo to identify uniquely a given chat 'space' on its
 * systems. Each lobby has a count of users, a count of voice chat users, and a
 * count of webcam users. See also YahooChatRoom and YahooChatCategory.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface YahooChatLobby<T extends YahooChatUser> {

	/**
	 * Adds a new user to this lobby. If the user already exists in this lobby,
	 * nothing happens.
	 * 
	 * @param yahooChatUser
	 *            The user to add to this lobby.
	 */
	void addUser(T yahooChatUser);
	
	/**
	 * Removes the user from this lobby, if it was in the lobby.
	 * 
	 * @param yahooChatUser
	 *            The user to remove from this lobby.
	 */
	void removeUser(T yahooChatUser);

	/**
	 * Removes all users from this lobby.
	 */
	void clearUsers();

	/**
	 * checks if the user exists in this lobby.
	 * 
	 * @param yahooChatUser
	 *            The user for which to check this lobby.
	 * @return ''true'' if the supplied user exists in this lobby, ''false''
	 *         otherwise.
	 */
	boolean exists(T yahooChatUser);

	/**
	 * Retrieves the user of this lobby matching the provided ID. Returns
	 * ''null'' if no such user exists.
	 * 
	 * @param id
	 *            The ID of the user to return.
	 * @return The user matching the ID, or ''null'' if no such user exists.
	 */
	T getUser(String id);

	/**
	 * Returns the lobby number of this object. The lobby number should be
	 * unique to the chatroom that this lobby is part of.
	 * 
	 * @return Lobby number of this lobby.
	 */
	int getLobbyNumber();

	/**
	 * Returns the amount of users currently in this lobby.
	 * 
	 * @return the number of users in this lobby.
	 */
	int getUserCount();

	/**
	 * Returns the network name of this Lobby. The name of the room and the
	 * number of the lobby (separated by a colon) form the 'network name' of the
	 * lobby. It is used by Yahoo to identify uniquely a given chat 'space' on
	 * its systems.
	 * 
	 * @return Network name of this Lobby object.
	 */
	String getNetworkName();

	/**
	 * Returns all users in this lobby.
	 * 
	 * @return All users in this lobby.
	 */
	Collection<T> getMembers();

	/**
	 * The listings of category/room/lobbies contain summaries on several
	 * attributes of each lobby. Examples of attributes that are reported are
	 * the number of webcam or voice capable users inside the room.
	 * 
	 * Note that these values represent a state view at the time the listing was
	 * refresh last. For more accurate values, try iterating over all users in a
	 * room.
	 * 
	 * @return the reported user count of this lobby, or -1 if no statistics are
	 *         available.
	 */
	int getReportedUsers();

	/**
	 * The listings of category/room/lobbies contain summaries on several
	 * attributes of each lobby. Examples of attributes that are reported are
	 * the number of webcam or voice capable users inside the room.
	 * 
	 * Note that these values represent a state view at the time the listing was
	 * refresh last. For more accurate values, try iterating over all users in a
	 * room.
	 * 
	 * @return the reported count of users that support voice in this lobby, or
	 *         -1 if no statistics are available.
	 */
	int getReportedVoices();

	/**
	 * The listings of category/room/lobbies contain summaries on several
	 * attributes of each lobby. Examples of attributes that are reported are
	 * the number of webcam or voice capable users inside the room.
	 * 
	 * Note that these values represent a state view at the time the listing was
	 * refresh last. For more accurate values, try iterating over all users in a
	 * room.
	 * 
	 * @return the reported count of users that have a webcam, or -1 if no
	 *         statistics are available.
	 */
	int getReportedWebcams();

	/**
	 * @return the parentRoomId
	 */
	long getParentRoomId();
}
