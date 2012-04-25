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
package org.openymsg.context.auth;

/**
 * Representation of authentication failure states.
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public enum AuthenticationFailure {
	/** Local implementation value used when Failure is not found */
	NO_REASON(-100),
	/** Local implementation value used when failure is in stage1 authentication url */
	STAGE1(-99),
	/** Local implementation value used when failure is in stage2 authentication url */
	STAGE2(-98),
	/** Account unknown? */
	BADUSERNAME(3),
	/** Bad Login? */
	BAD(13),
	/** Usually too many failed attempts */
	LOCKED(14),
	/** Bad login? */
	BAD2(29),
	/** Bad username/password ? */
	INVALID_CREDENTIALS(1013);

	private long value;

	/**
	 * Creates a new state, based on a unique long value identifier.
	 * @param value Unique long value for the state to be created.
	 */
	private AuthenticationFailure(long value) {
		this.value = value;
	}

	/**
	 * Gets the (unique) long value that identifies this status.
	 * @return long value identifying this status.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Returns the AuthenticationFailure that is identified by the provided long value. This method throws an
	 * IllegalArgumentException if no matching AuthenticationFailure is defined in this enumeration.
	 * @param value AuthenticationState identifier.
	 * @return AuthenticationState identified by 'value'.
	 * @throws IllegalArgumentException if no status found matching value
	 */
	public static AuthenticationFailure getStatus(long value) throws IllegalArgumentException {
		final AuthenticationFailure[] all = AuthenticationFailure.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getValue() == value) {
				return all[i];
			}
		}

		throw new IllegalArgumentException("No AuthenticationFailure matching long value '" + value + "'.");
	}
}
