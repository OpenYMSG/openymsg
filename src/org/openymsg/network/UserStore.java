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

import java.util.Hashtable;

/**
 * A cache of YahooUser objects. The same user may be encountered through
 * different means - this class attempts to ensure a canonical object is used to
 * represent a given user. (Note: identities of the same user will have
 * different objects, as they 'appear' to be different users.)
 * 
 * 'Encounters' may be as a result of a user being on your friends list, because
 * someone sent you a message/invite/request or because they were in a chatroom
 * the user visited.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class UserStore {
	private Hashtable<String, YahooUser> users = new Hashtable<String, YahooUser>();

	/**
	 * Query hash for specific user.
	 * 
	 * @param id
	 *            Yahoo ID of the user to check for.
	 * @return ''true'' if a user identified by the provided ID is present in
	 *         this userstore, ''false'' otherwise.
	 */
	public boolean contains(String id) {
		return users.containsKey(id);
	}

	/**
	 * Returns the YahooUser matching the id, or 'null' if no such user exists
	 * in this userstore.
	 * 
	 * @param id
	 *            The ID of the user to return.
	 * @return YahooUser matching the ID, or 'null' if no such user exists.
	 */
	public YahooUser get(String id) {
		return users.get(id);
	}

	/**
	 * Get a user object, or create if not known already
	 * 
	 * @param id
	 *            ID of the YahooUser to return. If no such user currently
	 *            exists in this UserStore object, one will be created.
	 * @return A user matching the ID.
	 */
	public YahooUser getOrCreate(String id) {
		if (!contains(id)) 
			users.put(id, new YahooUser(id));
		return get(id);
	}

	/**
	 * Get and update a user object, or create if not known already.
	 * 
	 * @param id
	 *            ID of the user to return.
	 * @param status
	 * @param onChat
	 * @param onPager
	 * @return A YahooUser matching the ID. All attributes of this user are set
	 *         to the attributes provided to this method.
	 */
	public YahooUser getOrCreate(String id, Status status, boolean onChat,
			boolean onPager) {
		if (!contains(id)) {
			return users.put(id, new YahooUser(id, status, onChat, onPager));
		}

		get(id).update(id, status, onChat, onPager);
		return get(id);
	}

	/**
	 * Used for things like conference invites. This method takes a array of ids
	 * and returns an array of associated YahooUser objects.
	 * 
	 * @param ids
	 *            array of YahooUser IDs
	 * @return an array of associated YahooUser objects
	 */
	public YahooUser[] toUserArray(String[] ids) {
		YahooUser[] dest = new YahooUser[ids.length];
		for (int i = 0; i < ids.length; i++)
			dest[i] = getOrCreate(ids[i]);
		return dest;
	}

	/**
	 * Returns all YahooUsers in this store.
	 * 
	 * @return All YahooUsers in this store.
	 */
	public Hashtable<String, YahooUser> getUsers() {
		return users;
	}
}
