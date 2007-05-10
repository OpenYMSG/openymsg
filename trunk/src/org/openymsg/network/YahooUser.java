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

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a single canonical represenation of a user on Yahoo.
 * For each user there is only ever one single instance of this class - as
 * updated details arrive, an existing object is sought for and updated. This is
 * to prevent code holding onto YahooUser references from pointing to stale
 * data.
 * 
 * The groupCount integer is used to service the isFriend() method. When added
 * to a group it is incremented, when removed it is decremented. When zero, it
 * means this user is not part of the client's friends list.
 * 
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
public class YahooUser // Cannot be serialised
{
	protected String id; // Yahoo id

	protected Status status; // Status (away, etc)

	protected int stealth; // Stealth status

	protected boolean onChat, onPager, ignored; // Flags

	protected boolean stealthBlock;

	protected String customStatusMessage; // Custom away

	protected boolean customStatusBusy; // Ditto

	private Set<YahooGroup> groups = new HashSet<YahooGroup>();

	private long idleTime = -1; // Idle time (-1 = unknown)

	/**
	 * CONSTRUCTORS Do not called these manually. UserStore is a cache of
	 * canonical YahooUser objects - use the getOrCreate() methods in that class
	 * so the user objects are correctly added to that cache.
	 */
	public YahooUser(String id, Status status, boolean onChat, boolean onPager) {
		update(id, status, onChat, onPager);
	}

	public YahooUser(String id) {
		this(id, Status.OFFLINE, false, false);
	}

	public void setIgnored(boolean i) {
		ignored = i;
	}

	public void setStealthBlocked(boolean i) {
		stealthBlock = i;
	}

	public void setCustom(String m, String a) {
		customStatusMessage = m;
		customStatusBusy = a.charAt(0) == '1';
	}

	public void setIdleTime(String secs) {
		idleTime = Long.parseLong(secs);
	}

	public void setStealth(int st) {
		stealth = st;
	}

	public void addGroup(YahooGroup group) {
		groups.add(group);
	}

	public String getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public int getStealth() {
		return stealth;
	}

	public boolean isOnChat() {
		return onChat;
	}

	public boolean isOnPager() {
		return onPager;
	}

	public boolean isLoggedIn() {
		return (onChat || onPager);
	}

	public boolean isIgnored() {
		return ignored;
	}

	public boolean isStealthBlocked() {
		return stealthBlock;
	}

	public String getCustomStatusMessage() {
		return customStatusMessage;
	}

	public boolean isCustomBusy() {
		return customStatusBusy;
	}

	public boolean isFriend() {
		return (getGroups().size() > 0);
	}

	public long getIdleTime() {
		return idleTime;
	}

	@Override
	public String toString() {
		return "YahooUser [ID=" + id + ", status="+status.name()+", ignored="+ignored+", stealthBlock="+stealthBlock+", customStatusMessage="+customStatusMessage+", isFriend="+isFriend()+"]";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof YahooUser)
			return  ((YahooUser) other).getId().equals(this.getId());
		else return super.equals(other);
	}



	/**
	 * Updates the YahooUser with the new values.
	 * 
	 * @param newId
	 *            replacement for current ID
	 * @param newStatus
	 *            replacement for current Status
	 * @param newVisibility
	 *            replacement for current onChat and onPager values
	 */
	public void update(String newId, Status newStatus, String newVisibility) {
		// This is the new version, where 13=combined pager/chat
		final int iVisibility = (newVisibility == null) ? 0 : Integer
				.parseInt(newVisibility);
		update(newId, newStatus, (iVisibility & 2) > 0, (iVisibility & 1) > 0);
	}

	/**
	 * Updates the YahooUser with the new values.
	 * 
	 * @param newId
	 *            replacement for current ID.
	 * @param newStatus
	 *            replacement for current Status value
	 * @param newOnChat
	 *            replacement for current onChat value
	 * @param newOnPager
	 *            replacement for current onPager value
	 */
	public void update(String newId, Status newStatus, boolean newOnChat,
			boolean newOnPager) {
		// This is the old version, when 13=pager and 17=chat
		this.id = newId;
		this.status = newStatus;
		this.onChat = newOnChat;
		this.onPager = newOnPager;

		if (this.status != Status.CUSTOM) {
			customStatusMessage = null;
			customStatusBusy = false;
		}
	}

	/**
	 * @return the groups
	 */
	public Set<YahooGroup> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(Set<YahooGroup> groups) {
		this.groups = groups;
	}
	
}
