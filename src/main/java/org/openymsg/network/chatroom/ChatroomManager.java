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
package org.openymsg.network.chatroom;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jdom.JDOMException;

/**
 * The Yahoo chatroom listing can be retrieved without being logged onto the
 * Yahoo IM network. This class manages this proceess
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public interface ChatroomManager<T extends YahooChatCategory<?,?>, U extends YahooChatLobby<?>> {
	/**
	 * This is the amount of seconds that's the minimum timeout between two
	 * seperate refreshes of the category tree for one particular locale prefix.
	 * Each implementation should wait at least this amount of time between
	 * refreshes.
	 */
	int REFRESH_TIMOUT_IN_SECONDS = 15 * 60;

	String PREFIX = "http://";

	String TOP_URL = "insider.msg.yahoo.com/ycontent/?chatcat=0";

	/**
	 * This method fetches the top level category. If the cookies are passed,
	 * Yahoo will not filter adult categories.
	 * 
	 * Note: the Yahoo servers prove to be unstable at times. This can cause
	 * connection timeouts.
	 * 
	 * @return ''true'' if the rootCategory was successfully loaded, ''false''
	 *         if an IOException occured while trying to execute the HTTP
	 *         request that should retrieve the categories, or if something
	 *         unexpected caused nothing to be returned.
	 * @throws IOException
	 * @throws JDOMException
	 * @throws MalformedURLException
	 */
	T loadCategories()
			throws MalformedURLException, JDOMException, IOException;
	/**
	 * Returns the lobby as returned by
	 * {@link YahooChatCategory#getLobby(String)} for the root category loaded
	 * under the localePrefix of this object. This method returns an
	 * IllegalStateException if the categories for this prefix have not been
	 * loaded yet (consider using {@link #loadCategories()} first).
	 * 
	 * @param networkName
	 *            The network name for the Lobby object that should be returned.
	 * @return Lobby object represented by the networkName.
	 */
	U getLobby(String networkName);
}
