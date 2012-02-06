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

/**
 * Representation of authentication result states.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public enum AuthenticationState {
    NO_REASON(-1), // don't know, this is when no reason is sent
    BAD(13), // Bad login?
    BAD2(29), // Bad login?
    LOCKED(14), // You've been naughty
    DUPLICATE_LOGIN(99),
    BADUSERNAME(3), // Account unknown?
    YAHOO_LOGOFF(-100), // Yahoo has told us to log off
    //42 - Account has signed in from another location
    INVALID_CREDENTIALS(1013),
    UNKNOWN_52(52); // don't know

    private long value;

    /**
     * Creates a new state, based on a unique long value identifier.
     * 
     * @param value
     *            Unique long value for the state to be created.
     */
    private AuthenticationState(long value) {
        this.value = value;
    }

    /**
     * Gets the (unique) long value that identifies this status.
     * 
     * @return long value identifying this status.
     */
    public long getValue() {
        return value;
    }

    /**
     * Returns the AuthenticationState that is identified by the provided long value. This method throws an
     * IllegalArgumentException if no matching AuthenticationState is defined in this enumeration.
     * 
     * @param value
     *            AuthenticationState identifier.
     * @return AuthenticationState identified by 'value'.
     */
    public static AuthenticationState getStatus(long value) {
        final AuthenticationState[] all = AuthenticationState.values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].getValue() == value) {
                return all[i];
            }
        }

        throw new IllegalArgumentException("No AuthenticationState matching long value '" + value + "'.");
    }
}
