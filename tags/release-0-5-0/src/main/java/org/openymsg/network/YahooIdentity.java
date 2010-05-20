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
 * Encapsulates a single identity that belongs to the current session. Yahoo enables users to have multiple personas via
 * identities (aka profiles). The 'primary' identity is the original Yahoo account, and all other identities are aliases
 * thereof.
 * 
 * Support for identities appears to have been tacked onto messenger after its initial release, being inconsistent and
 * patchy in its implementation. For example, while much of the protocol supports secondary identities (even logging
 * into Yahoo messenger using a secondary identity is possible) other parts can only accept primary identities - for
 * example sending a notify packet tagged with a secondary identity will result in the Yahoo server re-tagging the
 * packet with the associated primary identity before it is delivered to its target.
 * 
 * This 're-tagging' opens up potential security/privacy problems, as packets such as notify can betray the senders true
 * identity. They can also confuse the hell out of a Yahoo client (even the official client) if two IM windows are open
 * to the same user under two or more of the identities, as all notify 'events' will be indicated in said users primary
 * identity.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YahooIdentity // Cannot be serialised
{
    protected final String id; // Yahoo id

    private boolean primary, login; // Is primary or login id?

    private boolean active;

    /**
     * CONSTRUCTOR
     */
    public YahooIdentity(final String id) {
        this.id = id.toLowerCase();
        active = true;
    }

    /**
     * Public accessors
     */
    public String getId() {
        return id;
    }

    public boolean isPrimaryIdentity() {
        return primary;
    }

    public boolean isLoginIdentity() {
        return login;
    }

    public boolean isActivated() {
        return active;
    }

    /**
     * Package (default) setters
     */
    public void setPrimaryIdentity(boolean b) {
        primary = b;
    }

    public void setLoginIdentity(boolean b) {
        login = b;
    }

    void setActivated(boolean b) {
        active = b;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("id=").append(id).append(" primaryID=").append(primary).append(" loginID=")
                .append(login).append(" activated=").append(active);
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof YahooIdentity)) {
            return false;
        }

        return ((YahooIdentity) other).getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        int hash = 4133;
        hash *= 37 + id.hashCode();
        hash *= 37 + (primary ? 1231 : 1237);
        hash *= 37 + (login ? 1231 : 1237);
        hash *= 37 + (active ? 1231 : 1237);
        return hash;
    }
}
