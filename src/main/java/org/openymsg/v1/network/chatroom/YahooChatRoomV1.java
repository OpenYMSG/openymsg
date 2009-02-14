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
package org.openymsg.v1.network.chatroom;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.network.Util;
import org.openymsg.network.chatroom.YahooChatRoom;

/**
 * Represents a single chat room, either public (Yahoo owned) or private (user
 * owned). Each room is divided into multiple independent chat spaces, known as
 * 'lobbies'.
 * 
 * Rooms are placed into a hierarchy structure of named categories. *
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 * 
 * @see YahooChatLobby
 * @see YahooChatCategory
 */
public class YahooChatRoomV1 implements YahooChatRoom<YahooChatLobbyV1>{
	private String name, rawName;

	private String topic;

	private long id; // Id code

	private boolean isPublic; // True=public, false=private

	private Set<YahooChatLobbyV1> lobbies; // YahooChatLobby objects

	public YahooChatRoomV1(long id, String rawName, String topic, boolean ac) {
		this.id = id;
		this.rawName = rawName;
		this.name = Util.entityDecode(this.rawName);
		this.topic = topic;
		this.isPublic = ac;
		lobbies = new HashSet<YahooChatLobbyV1>();
	}

	/**
	 * Creates a new lobby inside this room.
	 * 
	 * @param lobbyNumber
	 *            The number of the lobby.
	 */
	public YahooChatLobbyV1 createLobby(int lobbyNumber) {
		final YahooChatLobbyV1 lobby = new YahooChatLobbyV1(rawName, id,
				lobbyNumber);
		addLobby(lobby);
		return lobby;
	}

	/**
	 * Adds the provided lobby to this room.
	 * 
	 * @param lobby
	 *            The lobby to add to this room.
	 */
	public void addLobby(YahooChatLobbyV1 lobby) {
		lobbies.add(lobby);
	}

	/**
	 * Checks if this room contains the provided lobby.
	 * 
	 * @param lobby
	 *            The lobby to search for in this room.
	 * @return ''true'' if the provided lobby exists in this room, ''false''
	 *         otherwise.
	 */
	public boolean containsLobby(YahooChatLobbyV1 lobby) {
		return lobbies.contains(lobby);
	}

	/**
	 * Returns all lobbies in this room.
	 * 
	 * @return All lobbies of this room.
	 */
	public Set<YahooChatLobbyV1> getLobbies() {
		return lobbies;
	}

	/**
	 * Returns the number of lobbies in this room.
	 * 
	 * @return the number of lobbies in this room.
	 */
	public int lobbyCount() {
		return lobbies.size();
	}

	/**
	 * The 'raw' name of this room is the unescaped name, as sent by the Yahoo
	 * domain.
	 * 
	 * @return The 'raw' roomname.
	 */
	public String getRawName() {
		return rawName;
	}

	/**
	 * Returns the 'pretty-print' name of this room. If you need to identify a
	 * room on the Yahoo network, use {@link #getRawName()} instead.
	 * 
	 * @return The (escaped) name of this room.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the topic of this room.
	 * 
	 * @return the topic of this room.
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Returns the long id of this room.
	 * 
	 * @return room id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Returns ''true'' if this room is a public room, ''false'' if it is a
	 * private one.
	 * 
	 * @return ''true'' if this room is "Yahoo owned" (public) or ''false'' if
	 *         this room is "user owned" (private).
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "YahooChatRoom '" + rawName + "'";
	}
}
