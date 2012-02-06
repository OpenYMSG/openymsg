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

/**
 * Representation of a general Yahoo Exception.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public abstract class YahooException extends Exception {
    private static final long serialVersionUID = 5508344016278756016L;

    /**
     * Creates a new instance and adds a more detailed description to it.
     * 
     * @param message
     *            the detail message.
     */
    public YahooException(String message) {
        super(message);
    }

    /**
     * Creates a new instance and adds a more detailed description to it.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the {@link Throwable} that caused this exception to be thrown.
     */
    public YahooException(String message, Throwable cause) {
        super(message, cause);
    }
}
