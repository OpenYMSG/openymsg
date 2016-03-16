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
package org.openymsg.legacy.network.chatroom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single chat lobby. Yahoo chatrooms consist of one or more numbered lobbies inside each public/private
 * room. The name of room and the number of the lobby (separated by a colon) form the 'network name' of the lobby - used
 * by Yahoo to identify uniquely a given chat 'space' on its systems. Each lobby has a count of users, a count of voice
 * chat users, and a count of webcam users. See also YahooChatRoom and YahooChatCategory.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YahooChatLobby {
    private int lobbyNumber;

    private final long parentRoomId;

    private final String networkName;

    private final Map<String, YahooChatUser> users;

    private int reportedWebcams = -1;

    private int reportedUsers = -1;

    private int reportedVoices = -1;

    /**
     * Creates a new lobby, identified by the provided lobby number.
     * 
     * Lobbies can have reported attributes. This happens when a listing of category/room/lobbies is retrieved, where
     * summaries are available. Examples of attributes that are reported are the number of webcam or voice capable users
     * inside the room.
     * 
     * @param parentRoomName
     *            The name of the room in which this lobby is being created.
     * @param parentRoomId
     *            The id of the room that this lobby belongs to.
     * @param lobbyNumber
     *            The number of this lobby.
     */
    public YahooChatLobby(String parentRoomName, long parentRoomId, int lobbyNumber) {
        this.lobbyNumber = lobbyNumber;
        this.parentRoomId = parentRoomId;
        users = new HashMap<String, YahooChatUser>();
        networkName = parentRoomName + ":" + this.lobbyNumber;
    }

    /**
     * Adds a new user to this lobby. If the user already exists in this lobby, nothing happens.
     * 
     * @param yahooChatUser
     *            The user to add to this lobby.
     */
    public void addUser(YahooChatUser yahooChatUser) {
        if (yahooChatUser == null) {
            throw new IllegalArgumentException("Argument 'yahooChatUser' cannot be null.");
        }

        users.put(yahooChatUser.getId(), yahooChatUser);
    }

    /**
     * Removes the user from this lobby, if it was in the lobby.
     * 
     * @param yahooChatUser
     *            The user to remove from this lobby.
     */
    public void removeUser(YahooChatUser yahooChatUser) {
        users.remove(yahooChatUser.getId());
    }

    /**
     * Removes all users from this lobby.
     */
    public void clearUsers() {
        users.clear();
    }

    /**
     * checks if the user exists in this lobby.
     * 
     * @param yahooChatUser
     *            The user for which to check this lobby.
     * @return ''true'' if the supplied user exists in this lobby, ''false'' otherwise.
     */
    public boolean exists(YahooChatUser yahooChatUser) {
        return users.containsKey(yahooChatUser.getId());
    }

    /**
     * Retrieves the user of this lobby matching the provided ID. Returns ''null'' if no such user exists.
     * 
     * @param id
     *            The ID of the user to return.
     * @return The user matching the ID, or ''null'' if no such user exists.
     */
    public YahooChatUser getUser(String id) {
        return users.get(id);
    }

    /**
     * Returns the lobby number of this object. The lobby number should be unique to the chatroom that this lobby is
     * part of.
     * 
     * @return Lobby number of this lobby.
     */
    public int getLobbyNumber() {
        return lobbyNumber;
    }

    /**
     * Returns the amount of users currently in this lobby.
     * 
     * @return the number of users in this lobby.
     */
    public int getUserCount() {
        return users.size();
    }

    /**
     * Returns the network name of this Lobby. The name of the room and the number of the lobby (separated by a colon)
     * form the 'network name' of the lobby. It is used by Yahoo to identify uniquely a given chat 'space' on its
     * systems.
     * 
     * @return Network name of this Lobby object.
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * Returns all users in this lobby.
     * 
     * @return All users in this lobby.
     */
    public Collection<YahooChatUser> getMembers() {
        return users.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "YahooChatLobby '" + networkName + "'";
    }

    /**
     * The listings of category/room/lobbies contain summaries on several attributes of each lobby. Examples of
     * attributes that are reported are the number of webcam or voice capable users inside the room.
     * 
     * Note that these values represent a state view at the time the listing was refresh last. For more accurate values,
     * try iterating over all users in a room.
     * 
     * @return the reported user count of this lobby, or -1 if no statistics are available.
     */
    public int getReportedUsers() {
        return reportedUsers;
    }

    /**
     * The listings of category/room/lobbies contain summaries on several attributes of each lobby. Examples of
     * attributes that are reported are the number of webcam or voice capable users inside the room.
     * 
     * Note that these values represent a state view at the time the listing was refresh last. For more accurate values,
     * try iterating over all users in a room.
     * 
     * @return the reported count of users that support voice in this lobby, or -1 if no statistics are available.
     */
    public int getReportedVoices() {
        return reportedVoices;
    }

    /**
     * The listings of category/room/lobbies contain summaries on several attributes of each lobby. Examples of
     * attributes that are reported are the number of webcam or voice capable users inside the room.
     * 
     * Note that these values represent a state view at the time the listing was refresh last. For more accurate values,
     * try iterating over all users in a room.
     * 
     * @return the reported count of users that have a webcam, or -1 if no statistics are available.
     */
    public int getReportedWebcams() {
        return reportedWebcams;
    }

    /**
     * Sets the number of reported users in this lobby.
     * 
     * @param reportedUsers
     *            The number of users that should be in this lobby, as reported by the Yahoo directory listing.
     */
    public void setReportedUsers(int reportedUsers) {
        this.reportedUsers = reportedUsers;
    }

    /**
     * Sets the number of reported users that support voice in this lobby.
     * 
     * @param reportedVoices
     *            The number of voice-capable users that should be in this lobby, as reported by the Yahoo directory
     *            listing.
     */
    public void setReportedVoices(int reportedVoices) {
        this.reportedVoices = reportedVoices;
    }

    /**
     * Sets the number of reported users that have webcams in this lobby.
     * 
     * @param reportedWebcams
     *            The number of webcam-capable users that should be in this lobby, as reported by the Yahoo directory
     *            listing.
     */
    public void setReportedWebcams(int reportedWebcams) {
        this.reportedWebcams = reportedWebcams;
    }

    /**
     * @return the parentRoomId
     */
    public long getParentRoomId() {
        return parentRoomId;
    }
}
