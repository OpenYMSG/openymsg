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

import java.util.Set;

import org.openymsg.network.Status;

/**
 * This class represents a single canonical representation of a user on Yahoo.
 * <p>
 * The UserId attribute is the primary attribute to distinguish between users,
 * and is the only field used in the equals() and hashCode() methods.
 * <p>
 * The groupCount integer is used to service the isFriend() method. When added
 * to a group it is incremented, when removed it is decremented. When zero, it
 * means this user is not part of the client's friends list.
 * <p>
 * Note: this API cannot guarantee the accuracy of details held on users who's
 * contact with you has expired. So... if you leave a chatroom in which 'fred'
 * was a member, 'fred's object will continue to be in the hashtable - BUT his
 * status will almost certainly not be updated any longer (unless, of course,
 * you have a link with him via some other route - like if he is on your friends
 * list too!)
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface YahooUser {
	/**
	 * Returns the user ID of this instance.
	 * @return the user ID of this instance.
	 */
	String getId();

	/**
	 * Checks if this user is on our ignore list, which indicates if the user is
	 * on the ignore list (in other words: the local user does not want to
	 * receive messages from this user).
	 * @return <tt>true</tt> if this user is on the ignore list.
	 */
	boolean isIgnored();


	/**
	 * Checks if this user is on our stealth-blocked list, disallowing it to see
	 * us.
	 * @return <tt>true</tt> if this user is on our StealthBlocked list.
	 */
	boolean isStealthBlocked();


	/**
	 * Returns the custom status message, or <tt>null</tt> if no such message
	 * has been set.
	 * @return The custom status message or <tt>null</tt>.
	 */
	String getCustomStatusMessage();

	/**
	 * Returns the custom status, or <tt>null</tt> if no such status has been
	 * set.
	 * @return The custom status or <tt>null</tt>.
	 */
	String getCustomStatus();

	/**
	 * Returns the amount of seconds that this user has been idle, or -1 if this
	 * is unknown.
	 * @return the amount of seconds that this user has been idle, or -1.
	 */
	long getIdleTime();

	/**
	 * Gets the stealth modus of this user.
	 * @return the stealth modus of this user.
	 */
	int getStealth();

	/**
	 * Returns an unmodifiable set of IDs of the groups that this user is in, or
	 * an empty set if this user is an anonymous user.
	 * @return the IDs of the groups that this user is in, or an empty set
	 *         (never <tt>null</tt>)
	 */
	Set<String> getGroupIds();

	/**
	 * Gets the presence status of this user.
	 * @return The presence status of this user.
	 */
	Status getStatus();

	boolean isOnChat();

	boolean isOnPager();

	boolean isLoggedIn();

	/**
	 * Checks if this user is on our contact list (and this can be considered 'a
	 * friend'), or if this user is an anonymous user. This method is the exact
	 * opposite of {@link #isAnonymous()}.
	 * @return <tt>true</tt> if this user is on our contact list,
	 *         <tt>false</tt> if this user is anonymous.
	 */
	boolean isFriend();

	/**
	 * Checks if this user is an anonymous user, or if he's a friend (if the
	 * user exists on our contact list the user is considered to be a friend).
	 * This method is the exact opposite of {@link #isFriend()}.
	 * 
	 * @return <tt>false</tt> if this user is on our contact list,
	 *         <tt>true</tt> if this user is anonymous.
	 */
	boolean isAnonymous();


	/**
	 * Updates the YahooUser with the new values.
	 * @param newStatus
	 *            replacement for current Status
	 * @param newVisibility
	 *            replacement for current onChat and onPager values
	 */
	void update(Status newStatus, String newVisibility);

	/**
	 * Updates the YahooUser with the new values.
	 * @param newStatus
	 *            replacement for current Status value
	 * @param newOnChat
	 *            replacement for current onChat value
	 * @param newOnPager
	 *            replacement for current onPager value
	 */
	void update(Status newStatus, boolean newOnChat, boolean newOnPager);
}
