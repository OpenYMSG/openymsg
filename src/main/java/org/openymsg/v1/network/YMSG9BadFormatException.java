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

/**
 * Exception indication an error during parsing of a YMSG9Packet.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YMSG9BadFormatException extends RuntimeException {

	private static final long serialVersionUID = 2722272866191388316L;

	/**
	 * The packet of which parsing caused this exception to be thrown.
	 */
	private final transient YMSG9Packet packet;

	/**
	 * Constructs a new YMSG9BadFormatException with the specified detail
	 * message.
	 * 
	 * @param message
	 *            Optional additional detail message.
	 * @param packet
	 *            Packet of which parsing caused this exception to be thrown.
	 * @param cause
	 *            The cause.
	 */
	public YMSG9BadFormatException(String message, YMSG9Packet packet,
			Throwable cause) {
		super(constructMessage(message, packet), cause);
		this.packet = packet;
	}

	/**
	 * Convenience method for constructing the detailled Exception message;
	 * 
	 * @param message
	 *            Optional additional detail message.
	 * @param packet
	 *            Packet of which parsing caused this exception to be thrown.
	 * @return A String object combining the text representation of both
	 *         parameters.
	 */
	private static String constructMessage(String message, YMSG9Packet packet) {
		final StringBuilder result = new StringBuilder("Bad format for packet");

		if (packet != null) {
			result.append(": [");
			result.append(packet.toString());
			result.append("]");
		}
		result.append('.');

		if (message != null) {
			result.append(System.getProperty("line.separator"));
			result.append(message);
		}
		return result.toString();
	}

	/**
	 * Returns the packet that caused this Exception.
	 * 
	 * @return the packet that caused this Exception.
	 */
	public YMSG9Packet getPacket() {
		return packet;
	}
}
