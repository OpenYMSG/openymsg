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
package org.openymsg.network.event;

import org.openymsg.network.YahooProtocol;


/**
 * @author Damian Minkov
 */
public class SessionAuthorizationEvent
    extends SessionEvent
{
	private static final long serialVersionUID = -4434174537165587365L;
	protected String fName = null;
	protected String lName = null;
	private YahooProtocol protocol = null;
    public YahooProtocol getProtocol() {
		return protocol;
	}

	protected boolean authorizationAccepted = false;
    protected boolean authorizationDenied = false;
    protected boolean authorizationRequest = false;

    public SessionAuthorizationEvent(Object o, String who, String msg, YahooProtocol protocol)
    {
        super(o, null, who, msg);
        this.protocol = protocol;
    }

    public SessionAuthorizationEvent(Object o,
            String id, String who, String fname, String lname, String msg,
            YahooProtocol protocol)
    {
        super(o, id, who, msg);
        this.fName = fname;
        this.lName = lname;
        this.protocol = protocol;
    }


    // -----------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------

    public void setStatus(int s)
    {
        super.setStatus(s);

        switch(s)
        {
            case 1:
                /* Authorization Accepted */
                authorizationAccepted = true;
                break;
            case 2:
                /* Authorization Denied */
                authorizationDenied = true;
                break;
            default:
                /* Authorization Request? */
                authorizationRequest = true;
                break;
        }
        super.setStatus(s);
    }

    public boolean isAuthorizationAccepted()
    {
        return authorizationAccepted;
    }

    public boolean isAuthorizationDenied()
    {
        return authorizationDenied;
    }

    public boolean isAuthorizationRequest()
    {
        return authorizationRequest;
    }

    public String toString()
    {
        return "to:"+getTo()+" from:"+getFrom()+" message:"+message;
    }
}
