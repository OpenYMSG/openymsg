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
 * Represents an exception that indicates a problem during logging into the Yahoo network. An optional status may be
 * supplied, which can contain more information as to the cause of the faillure.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class LoginRefusedException extends YahooException {

    private static final long serialVersionUID = 7520723347704202477L;

    private AuthenticationState status;

    /**
     * Creates a new Exception that indicates a problem during logging into the Yahoo network.
     * 
     * @param message
     *            Message describing the exception.
     */
    public LoginRefusedException(String message) {
        super(message);
    }

    /**
     * Creates a new Exception that indicates a problem during logging into the Yahoo network, caused by another
     * exception.
     * 
     * @param message
     *            Message describing the exception.
     * @param cause
     *            The {@link Throwable} that caused this exception to be thrown.
     */
    public LoginRefusedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new Exception that indicates a problem during logging into the Yahoo network.
     * 
     * @param message
     *            Message describing the exception.
     * @param status
     *            Status indicator of the problem.
     */
    public LoginRefusedException(String message, AuthenticationState status) {
        this(message);
        this.status = status;
    }

    /**
     * Returns the status associated with this problem, or -1 if no status was set.
     * 
     * @return Status
     */
    public AuthenticationState getStatus() {
        return status;
    }
}
