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

import java.util.Set;

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
public interface YahooChatRoom<T extends YahooChatLobby<?>> {

	/**
	 * Creates a new lobby inside this room.
	 * 
	 * @param lobbyNumber
	 *            The number of the lobby.
	 */
	T createLobby(int lobbyNumber);

	/**
	 * Adds the provided lobby to this room.
	 * 
	 * @param lobby
	 *            The lobby to add to this room.
	 */
	void addLobby(T lobby);

	/**
	 * Checks if this room contains the provided lobby.
	 * 
	 * @param lobby
	 *            The lobby to search for in this room.
	 * @return ''true'' if the provided lobby exists in this room, ''false''
	 *         otherwise.
	 */
	boolean containsLobby(T lobby);

	/**
	 * Returns all lobbies in this room.
	 * 
	 * @return All lobbies of this room.
	 */
	Set<T> getLobbies();

	/**
	 * Returns the number of lobbies in this room.
	 * 
	 * @return the number of lobbies in this room.
	 */
	int lobbyCount();

	/**
	 * The 'raw' name of this room is the unescaped name, as sent by the Yahoo
	 * domain.
	 * 
	 * @return The 'raw' roomname.
	 */
	String getRawName();

	/**
	 * Returns the 'pretty-print' name of this room. If you need to identify a
	 * room on the Yahoo network, use {@link #getRawName()} instead.
	 * 
	 * @return The (escaped) name of this room.
	 */
	String getName();

	/**
	 * Returns the topic of this room.
	 * 
	 * @return the topic of this room.
	 */
	String getTopic();

	/**
	 * Returns the long id of this room.
	 * 
	 * @return room id.
	 */
	long getId();

	/**
	 * Returns ''true'' if this room is a public room, ''false'' if it is a
	 * private one.
	 * 
	 * @return ''true'' if this room is "Yahoo owned" (public) or ''false'' if
	 *         this room is "user owned" (private).
	 */
	boolean isPublic();

}
