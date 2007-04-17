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
package org.openymsg.network;

import java.util.Vector;

/**
 * As conference packets can be received in an inconvenient order, this class
 * carries a lot of code to compensate. Conference packets can actually arrive
 * both before and (probably) after the formal lifetime of the conference (from
 * invite received/accepted to logoff).
 * 
 * Packets which arrive before an invite are buffered. When an invite arrives
 * the packets are fetched and the buffer null'd (which is then used as a flag
 * to determine whether an invite has arrived or not). By using this method, the
 * API user will *ALWAYS* get an invite before any other packets.
 * 
 * The closed flag marks a closed conference. Packets arriving after this time
 * should be ignored.
 * 
 * The users list should not contain any of our user's own identities. This is
 * why they are screened out by the addUser/addUsers methods.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YahooConference // Cannot be serialised
{
	protected Vector<YahooUser> users; // YahooUser's in this conference

	protected String room; // Room name

	private boolean closed; // Conference has been exited?

	private Vector<YMSG9Packet> packetBuffer; // Buffer packets before invite

	private Session parent; // Parent session object

	private YahooIdentity identity; // Yahoo identity for this conf.

	private UserStore userStore; // Canonical user list

	/**
	 * CONSTRUCTOR Note: the first constructor is used when *we* create a
	 * conference, the second is used when we are invited to someone else's
	 * conference. When *we* create a conference, there is no need to buffer
	 * packets prior to an invite.
	 */
	YahooConference(UserStore us, YahooIdentity yid, String r, Session ss,
			boolean b) {
		userStore = us;
		identity = yid;
		users = new Vector<YahooUser>();
		parent = ss;
		room = r;
		closed = false;
		if (b)
			packetBuffer = new Vector<YMSG9Packet>();
		else
			packetBuffer = null;
	}

	YahooConference(UserStore us, YahooIdentity yid, String r, Session ss) {
		this(us, yid, r, ss, true);
	}

	/**
	 * The closed flag is set when this conference is exited. All further
	 * packets from this conference should be ignored.
	 */
	void closeConference() {
		closed = true;
	}

	/**
	 * Public accessors
	 */
	public String getName() {
		return room;
	}

	public boolean isClosed() {
		return closed;
	}

	public Vector<YahooUser> getMembers() {
		return new Vector<YahooUser>(users);
	}

	public YahooIdentity getIdentity() {
		return identity;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("name=").append(room).append(
				" users=").append(users.size()).append(" id=").append(
				identity.getId()).append(" closed?=").append(closed);
		return sb.toString();
	}

	/**
	 * The packetBuffer object is created when the conference is created and set
	 * to null when the conference invite actually arrives.
	 */
	// Have we been invited yet?
	boolean isInvited() {
		return (packetBuffer == null);
	}

	// We're received an invite, change status and return buffer
	Vector<YMSG9Packet> inviteReceived() {
		Vector<YMSG9Packet> v = packetBuffer;
		packetBuffer = null;
		return v;
	}

	// Add a packet to the buffer
	void addPacket(YMSG9Packet ev) {
		if (packetBuffer == null)
			throw new IllegalStateException(
					"Cannot buffer packets, invite already received");
		packetBuffer.addElement(ev);
	}

	/**
	 * Add to and get user list
	 */
	Vector<YahooUser> getUsers() {
		return users;
	}

	synchronized void addUsers(String[] usernames) {
		for (String username : usernames)
			addUser(username);
	}

	synchronized void addUser(String u) {
		if (!exists(u) && !parent.isValidYahooID(u)) {
			users.addElement(userStore.getOrCreate(u));
		}
	}

	synchronized void removeUser(String u) {
		for (int i = 0; i < users.size(); i++) {
			if (users.elementAt(i).getId().equals(u)) {
				users.removeElementAt(i);
				return;
			}
		}
	}

	/**
	 * Does a user exist (uses .equals() on user id)
	 */
	private boolean exists(String s) {
		for (YahooUser user : users) {
			if (user.getId().equals(s)) {
				return true;
			}
		}
		return false;
	}
}
