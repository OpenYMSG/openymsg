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
import java.net.SocketException;

import org.openymsg.network.ServiceType;
import org.openymsg.network.Util;
import org.openymsg.network.connection.PacketBodyBuffer;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class DirectConnectionHandler extends ConnectionHandler {
	private static final int[] EMPTY_FALLBACKS = new int[0];
	private YMSG9Socket socket;

	public DirectConnectionHandler(String h, int p) {
		this(h, p, EMPTY_FALLBACKS);
	}

	public DirectConnectionHandler(int p) {
		this(Util.directHost(), p);
	}

	DirectConnectionHandler(String h, int p, int[] fallBackPorts) {
		this.socket = new YMSG9Socket(h, p, fallBackPorts);
	}
	public DirectConnectionHandler(boolean fl) {
		this(Util.directHost(), Util.directPort(), fl ? Util.directPorts() : EMPTY_FALLBACKS);
	}

	public DirectConnectionHandler() {
		this(Util.directHost(), Util.directPort(), EMPTY_FALLBACKS);
	}

	public String getHost() {
		return this.socket.getHost();
	}

	public int getPort() {
		return this.socket.getPort();
	}

	/**
	 * Session calls this when a connection handler is installed
	 */
	@Override
	void install(SessionV1 ss) {
		// session=ss;
	}

	/**
	 * Opens a socket to Yahoo IM, or throws an exception. If fallback ports are
	 * to be used, will attempt each port in turn - upon failure will return the
	 * last exception (the one for the final port).
	 */
	@Override
	void open() throws SocketException, IOException {
		this.socket.open();
	}

	@Override
	void close() throws IOException {
		if (socket != null)
			socket.close();
	}

	/**
	 * Note: the term 'packet' here refers to a YMSG message, not a TCP packet
	 * (although in almost all cases the two will be synonymous). This is to
	 * avoid confusion with a 'YMSG message' - the actual discussion packet.
	 * 
	 * service - the Yahoo service number status - the Yahoo status number (not
	 * sessionStatus!) body - the payload of the packet
	 * 
	 * Note: it is assumed that 'ops' will have been set by the time this method
	 * is called.
	 */
	@Override
	protected void sendPacket(PacketBodyBuffer body, ServiceType service,
			long status, long sessionId) throws IOException {
		this.socket.sendPacket(body, 12, service, status, sessionId);
	}

	/**
	 * Return a Yahoo message
	 */
	@Override
	protected YMSG9Packet receivePacket() throws IOException {
		return this.socket.receivePacket();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Direct connection: ").append(getHost())
				.append(":").append(getPort());
		return sb.toString();
	}
}
