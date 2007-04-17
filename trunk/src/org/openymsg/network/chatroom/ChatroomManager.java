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
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.Util;

/**
 * The Yahoo chatroom listing can be retrieved without being logged onto the
 * Yahoo IM network. This class manages this proceess
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class ChatroomManager {
	/**
	 * This is the amount of seconds that's the minimum timeout between two
	 * seperate refreshes of the category tree for one particular locale prefix.
	 * Each implementation should wait at least this amount of time between
	 * refreshes.
	 */
	public final static int REFRESH_TIMOUT_IN_SECONDS = 15 * 60;

	public final static String PREFIX = "http://";

	public final static String TOP_URL = "insider.msg.yahoo.com/ycontent/?chatcat=0";

	/**
	 * all Locale Prefix / Root Category combinations.
	 * 
	 */
	private final static Map<String, YahooChatCategory> roots = new HashMap<String, YahooChatCategory>();

	/**
	 * map of timestamp values indicating when the specified localePrefix was
	 * last updated;
	 * 
	 */
	private final static Map<String, Long> timestamps = new HashMap<String, Long>();

	private final String cLine;

	private final String localePrefix;

	public ChatroomManager(String[] sessionCookies, String localePrefix) {
		if (sessionCookies != null) {
			cLine = /* FIXME "Cookie: "+ */
			sessionCookies[NetworkConstants.COOKIE_Y] + "; "
					+ sessionCookies[NetworkConstants.COOKIE_T];
		} else {
			cLine = null;
		}

		this.localePrefix = (localePrefix == null) ? "" : (localePrefix
				.endsWith(".") ? localePrefix : localePrefix + ".");

	}

	/**
	 * Recursively loads all YahooCategories that are sub-categories of the
	 * provided parent category.
	 * 
	 * @param parentElement
	 *            XML Element that describes the parent category.
	 * @param parentCategory
	 *            Parent Category to which the child categories are to be
	 *            appended.
	 * @param cLine
	 *            Cookie-lines to determine what categories and/or rooms to
	 *            load.
	 */
	@SuppressWarnings("unchecked")
	private void recursiveLoad(Element parentElement,
			YahooChatCategory parentCategory, String cLine) {
		List<Element> childCategories = parentElement.getChildren("category");

		for (Element e : childCategories) {
			final long id = Long.parseLong(e.getAttributeValue("id"));
			final String name = e.getAttributeValue("name");
			final YahooChatCategory yahooCategory = new YahooChatCategory(id,
					name, cLine, localePrefix);
			recursiveLoad(e, yahooCategory, cLine);
			parentCategory.addSubcategory(yahooCategory);
		}
	}

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
	public synchronized YahooChatCategory loadCategories()
			throws MalformedURLException, JDOMException, IOException {
		final long currentTimestamp = System.currentTimeMillis();

		if (timestamps.containsKey(localePrefix)) {
			final long timeout = timestamps.get(localePrefix);
			if (timeout + (REFRESH_TIMOUT_IN_SECONDS * 1000) > currentTimestamp) {
				// if timeout has not passed, return the cached object.
				return roots.get(localePrefix);
			}
		}

		// add or update the cache, then return that new update.
		final String addr = PREFIX + localePrefix + TOP_URL;
		URLConnection uConn = new URL(addr).openConnection();
		Util.initURLConnection(uConn);

		if (cLine != null) {
			uConn.setRequestProperty("Cookie", cLine);
		}

		final SAXBuilder builder = new SAXBuilder();
		final Document doc = builder.build(new URL(addr));

		final Element content = doc.getRootElement();
		final YahooChatCategory root = new YahooChatCategory(Long
				.parseLong(content.getAttributeValue("time")), "<root>", cLine,
				localePrefix);
		recursiveLoad(content.getChild("chatCategories"), root, cLine);

		timestamps.put(localePrefix, currentTimestamp);
		roots.put(localePrefix, root);

		return root;
	}

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
	public YahooChatLobby getLobby(String networkName) {
		final YahooChatCategory root = roots.get(localePrefix);
		if (root == null) {
			throw new IllegalStateException("Root category for this locale ("
					+ localePrefix + ") has not been loaded yet.");
		}

		return root.getLobby(networkName);
	}
}
