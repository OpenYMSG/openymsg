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
package org.openymsg.legacy.network;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * As conference packets can be received in an inconvenient order, this class carries a lot of code to compensate.
 * Conference packets can actually arrive both before and (probably) after the formal lifetime of the conference (from
 * invite received/accepted to logoff).
 * 
 * Packets which arrive before an invite are buffered. When an invite arrives the packets are fetched and the buffer
 * null'd (which is then used as a flag to determine whether an invite has arrived or not). By using this method, the
 * API user will *ALWAYS* get an invite before any other packets.
 * 
 * The closed flag marks a closed conference. Packets arriving after this time should be ignored.
 * 
 * The users list should not contain any of our user's own identities. This is why they are screened out by the
 * addUser/addUsers methods.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YahooConference // Cannot be serialised
{
    private static final Log log = LogFactory.getLog(YahooConference.class);
    
    protected Set<YahooUser> users; // YahooUser's in this conference

    protected String room; // Room name

    protected String message; // message

    private boolean closed; // Conference has been exited?

    private Queue<YMSG9Packet> packetBuffer; // Buffer packets before invite

    private Session parent; // Parent session object

    private YahooIdentity identity; // Yahoo identity for this conf.

    /**
     * CONSTRUCTOR Note: the first constructor is used when *we* create a conference, the second is used when we are
     * invited to someone else's conference. When *we* create a conference, there is no need to buffer packets prior to
     * an invite.
     */
    YahooConference(YahooIdentity yid, String room, String message, Session ss, boolean b) {
        identity = yid;
        users = new HashSet<YahooUser>();
        parent = ss;
        this.room = room;
        this.message = message;
        closed = false;
        if (b)
            packetBuffer = new LinkedList<YMSG9Packet>();
        else
            packetBuffer = null;
    }

    YahooConference(YahooIdentity yid, String room, String message, Session ss) {
        this(yid, room, message, ss, true);
    }

    /**
     * The closed flag is set when this conference is exited. All further packets from this conference should be
     * ignored. Until reinvited
     */
    void closeConference() {
        closed = true;
    }

    /**
     * Reinvited to the conference
     */
    void reopenConference() {
        closed = false;
    }

    public String getName() {
        return room;
    }

    public boolean isClosed() {
        return closed;
    }

    public Set<YahooUser> getMembers() {
        return new HashSet<YahooUser>(users);
    }

    public YahooIdentity getIdentity() {
        return identity;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("name=");
        try {
            sb.append(room).append(" users=").append(users.size()).append(" id=")
                    .append(identity.getId()).append(" closed?=").append(closed);
        }
        catch (Exception e) {
            log.error("Failed toString on conference: " + this.room);
        }
        return sb.toString();
    }

    /**
     * The packetBuffer object is created when the conference is created and set to null when the conference invite
     * actually arrives.
     */
    // Have we been invited yet?
    boolean isInvited() {
        return (packetBuffer == null);
    }

    // We're received an invite, change status and return buffer
    Queue<YMSG9Packet> inviteReceived() {
        Queue<YMSG9Packet> v = null;
        if (packetBuffer != null) {
            v = new LinkedList<YMSG9Packet>(packetBuffer);
        }
        else
            v = new LinkedList<YMSG9Packet>();
        packetBuffer = null;
        return v;
    }

    // Add a packet to the buffer
    void addPacket(YMSG9Packet packet) {
        if (packetBuffer == null) throw new IllegalStateException("Cannot buffer packets, invite already received");
        packetBuffer.add(packet);
    }

    /**
     * Returns all users in this conference.
     * 
     * @return All users in this conference
     */
    public Set<YahooUser> getUsers() {
        return users;
    }

    /**
     * Adds an array of users (based on their usernames) to this conference.
     * 
     * @param usernames
     *            Names of all users to add
     */
    synchronized void addUsers(String[] usernames) {
        for (String username : usernames)
            addUser(username);
    }

    /**
     * Adds a user (based on his username) to this conference.
     * 
     * @param username
     *            Name of the user to add
     */
    synchronized void addUser(String username) {
        if (!exists(username) && !parent.isValidYahooID(username)) {
            users.add(new YahooUser(username));
        }
    }

    /**
     * Removes a user from this conference.
     * 
     * @param username
     *            Name of the user to remove
     */
    synchronized void removeUser(String username) {
        YahooUser removeMe = null;
        for (YahooUser user : users) {
            if (user.getId().equals(username)) {
                removeMe = user;
                break;
            }
        }

        if (removeMe != null) {
            users.remove(removeMe);
        }
    }

    /**
     * Does a user exist (uses .equals() on user id)
     * 
     * @return ''true'' if a user matching the username exists in this conference, ''false'' otherwise.
     */
    private boolean exists(String username) {
        for (YahooUser user : users) {
            if (user.getId().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String getMessage() {
        return message;
    }
}
