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
package org.openymsg.legacy.network.event;

import org.openymsg.legacy.network.ServiceType;

/**
 * Message Service Code errorPacketReceived y y (or null) y (or -1)
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionErrorEvent extends SessionEvent {
    protected int code = -1;

    protected ServiceType service;

    /**
     * CONSTRUCTORS
     */
    public SessionErrorEvent(Object o, String m, ServiceType sv) {
        super(o);
        message = m;
        service = sv;
    }

    public void setCode(int c) {
        code = c;
    }

    public ServiceType getService() {
        return service;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Error: message=\"").append(message).append("\" service=0x").append(
                Integer.toHexString(service.getValue()));
        return sb.toString();
    }
}
