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
package org.openymsg;

/**
 * Enumeration of all presence states.
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public enum YahooStatus {
	OFFLINE(-100), // Internal missing value
	ERROR(-1),
	AVAILABLE(0),
	BRB(1),
	BUSY(2),
	NOTATHOME(3),
	NOTATDESK(4),
	NOTINOFFICE(5),
	ONPHONE(6),
	ONVACATION(7),
	OUTTOLUNCH(8),
	STEPPEDOUT(9),
	AWAY(10),
	INVISIBLE(12),
	CUSTOM(99),
	IDLE(999);
	// OFFLINE(0x5a55aa56),
	// WEBLOGIN(0x5a55aa55),
	// TYPING(0x16);

	// Unique long representation of this Status.
	private long value;

	/**
	 * Creates a new Status, based on a unique long value identifier.
	 * @param value Unique long value for the Status to be created.
	 */
	private YahooStatus(long value) {
		this.value = value;
	}

	/**
	 * Gets the (unique) long value that identifies this Status.
	 * @return long value identifying this Status.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Returns the Status that is identified by the provided long value. This method throws an IllegalArgumentException
	 * if no matching Status is defined in this enumeration.
	 * @param value Status identifier.
	 * @return Status identified by 'value'.
	 */
	public static YahooStatus getStatus(long value) {
		final YahooStatus[] all = YahooStatus.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getValue() == value) {
				return all[i];
			}
		}

		throw new IllegalArgumentException("No Status matching long value '" + value + "'.");
	}

	public boolean isCustom() {
		return this == YahooStatus.CUSTOM;
	}
}
