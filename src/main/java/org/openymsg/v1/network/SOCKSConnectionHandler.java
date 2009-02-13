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

import java.util.Properties;

import org.openymsg.network.NetworkConstants;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SOCKSConnectionHandler extends DirectConnectionHandler {
	private String socksHost; // Socks service host

	private int socksPort; // Socks service port

	/**
	 * CONSTRUCTOR Reads the SOCKS setting from Java properties.
	 */
	public SOCKSConnectionHandler() throws IllegalArgumentException {
		socksHost = System.getProperty(NetworkConstants.SOCKS_HOST, "");
		socksPort = Integer.parseInt(System.getProperty(
				NetworkConstants.SOCKS_PORT, "-1"));
		if (socksHost.length() <= 0 || socksPort <= 0)
			throw new IllegalArgumentException("Bad SOCKS proxy properties: "
					+ socksHost + ":" + socksPort);
		System.getProperties().put(NetworkConstants.SOCKS_SET, "true");
	}

	/**
	 * CONSTRUCTOR Sets specific SOCKS server/port. Note: these settings will be
	 * global to all Socket's across the JVM.
	 */
	public SOCKSConnectionHandler(String h, int p) {
		socksHost = h;
		socksPort = p;
		Properties pr = System.getProperties();
		pr.put(NetworkConstants.SOCKS_HOST, socksHost);
		pr.put(NetworkConstants.SOCKS_PORT, socksPort + "");
		pr.put(NetworkConstants.SOCKS_SET, "true");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("SOCKS connection: ").append(
				socksHost).append(":").append(socksPort);
		return sb.toString();
	}
}
