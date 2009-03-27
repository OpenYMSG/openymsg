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
package org.openymsg.v1.network;

import java.util.LinkedList;
import java.util.Queue;

import org.openymsg.network.AbstractYahooConference;
import org.openymsg.network.YahooIdentity;

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
public class YahooConferenceV1 extends AbstractYahooConference<YahooUserV1, SessionV1>
{
	private Queue<YMSG9Packet> packetBuffer; // Buffer packets before invite

	/**
	 * CONSTRUCTOR Note: the first constructor is used when *we* create a
	 * conference, the second is used when we are invited to someone else's
	 * conference. When *we* create a conference, there is no need to buffer
	 * packets prior to an invite.
	 */
	YahooConferenceV1(YahooIdentity yid, String r, SessionV1 ss, boolean b) {
		super(yid, r, ss, b);
		if (b)
			packetBuffer = new LinkedList<YMSG9Packet>();
		else
			packetBuffer = null;
	}

	YahooConferenceV1(YahooIdentity yid, String r, SessionV1 ss) {
		this(yid, r, ss, true);
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
	Queue<YMSG9Packet> inviteReceived() {
		Queue<YMSG9Packet> v = null;
		if (packetBuffer != null) {
			v = new LinkedList<YMSG9Packet>(packetBuffer);
		} else
			v = new LinkedList<YMSG9Packet>();
		packetBuffer = null;
		return v;
	}

	// Add a packet to the buffer
	void addPacket(YMSG9Packet packet) {
		if (packetBuffer == null)
			throw new IllegalStateException(
					"Cannot buffer packets, invite already received");
		packetBuffer.add(packet);
	}

}
