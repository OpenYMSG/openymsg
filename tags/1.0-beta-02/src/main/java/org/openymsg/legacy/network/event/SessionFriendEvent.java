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

import org.openymsg.legacy.network.YahooUser;

/**
 * Represents an event triggered by a change in the presence or roster change of a session. This Event typically gets
 * thrown after a friend has been added, removed or updated.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionFriendEvent extends SessionEvent {
    private static final long serialVersionUID = -3163616489244193645L;
    protected final YahooUser user;
    private String groupName = null;

    public SessionFriendEvent(Object source, YahooUser user, String groupName) {
        this(source, user);
        this.groupName = groupName;
    }

    public SessionFriendEvent(Object source, YahooUser user) {
        super(source);

        if (user == null) {
            throw new IllegalArgumentException("Argument 'user' cannot be null.");
        }
        this.user = user;
    }

    public YahooUser getUser() {
        return user;
    }

    @Override
    public String getFrom() {
        return user.getId();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append(" friend:");
        sb.append(user.getId());
        return sb.toString();
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    public boolean isFailure() {
        return false;
    }
}
