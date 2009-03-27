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

import java.io.IOException;

import org.openymsg.network.NetworkConstants;
import org.openymsg.network.ServiceType;
import org.openymsg.network.connection.PacketBodyBuffer;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public abstract class ConnectionHandler {
	public final static byte PROTOCOL = 0x0c; 

	public final static byte[] MAGIC = { 'Y', 'M', 'S', 'G' };

	public final static byte[] VERSION = { 0x00, PROTOCOL, 0x00, 0x00 };

	public final static byte[] VERSION_HTTP = { PROTOCOL, 0x00, (byte) 0xc8, 0x00 };

	public final static int YMSG9_HEADER_SIZE = 20;

	abstract void install(SessionV1 session);

	abstract void open() throws IOException;

	abstract void close() throws IOException;

	abstract void sendPacket(PacketBodyBuffer body, ServiceType service,
			long status, long sessionID) throws IOException;

	abstract YMSG9Packet receivePacket() throws IOException;

	/**
	 * Creates an string array from cookies in packet.
	 * 
	 * @param pkt
	 *            Packet to extract the cookies from.
	 * @return Array of cookies.
	 */
	protected static String[] extractCookies(YMSG9Packet pkt) {
		String[] cookies = new String[3];
		for (int i = 0; i < cookies.length; i++) {
			String s = pkt.getNthValue("59", i);
			if (s == null)
				break;
			if (s.indexOf(";") >= 0)
				s = s.substring(0, s.indexOf(";"));
			switch (s.charAt(0)) {
			case 'Y':
				cookies[NetworkConstants.COOKIE_Y] = "Y=" + s.substring(2);
				break;
			case 'T':
				cookies[NetworkConstants.COOKIE_T] = "T=" + s.substring(2);
				break;
			case 'C':
				cookies[NetworkConstants.COOKIE_C] = "C=" + s.substring(2);
				break;
			default:
				throw new IllegalStateException("Unknown cookie: " + s);
			}
		}
		return cookies;
	}
}
