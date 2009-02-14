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
package org.openymsg.v1.network.chatroom;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openymsg.network.Util;
import org.openymsg.network.chatroom.ChatroomManager;
import org.openymsg.network.chatroom.YahooChatCategory;

/**
 * Categories are like directories. Each category may contain more categories
 * (sub directories), a list of public chatrooms and a list of private
 * chatrooms. Each room is further sub-divided into lobbies which hold a limited
 * number of users.
 * 
 * NOTE: this is the second implementation of this class. The original used
 * Yahoo's old method of accessing category/room data. They have now dropped
 * that scheme in favour of an XML based approach.
 * 
 * Categories are modelled by the YahooChatCategory class, rooms by the
 * YahooChatRoom class, and lobbies by (shock horror!) the YahooChatLobby class.
 * 
 * The data is delivered from Yahoo via a call the below URL, and in the
 * following format :
 * 
 * http://insider.msg.yahoo.com/ycontent/?chatcat=0
 * 
 * Resulting in (indented to improve readability) ...
 * 
 * <pre>
 * &lt;content time=&quot;1061459725&quot;&gt;
 * 	 &lt;chatCategories&gt; 
 *     &lt;category id=&quot;1600000002&quot; name=&quot;Business &amp; Finance&quot;&gt;
 *     [ Other categories may be nested here, to any level ]
 *     &lt;/category&gt;
 *     [ More categories ]
 *   &lt;/chatCategories&gt;
 * &lt;/content&gt;
 * </pre>
 * 
 * Rooms inside a category are fetched using the following URL, including the
 * room id encoded on the end :
 * 
 * http://insider.msg.yahoo.com/ycontent/?chatroom_&lt;id&gt;
 * 
 * Resulting in (indented for readability)...
 * 
 * <pre>
 * &lt;content time=&quot;1055350260&quot;&gt;
 *   &lt;chatRooms&gt;
 *     &lt;room
 *         type=&quot;yahoo&quot;
 *         id=&quot;1600326587&quot;
 *         name=&quot;Computers Lobby&quot;
 *         topic=&quot;Chat on your phone at http://messenger.yahoo.com/messenger/wireless/&quot;&gt;
 *       &lt;lobby count=&quot;12&quot; users=&quot;1&quot; voices=&quot;1&quot; webcams=&quot;0&quot; /&gt;
 *       &lt;lobby count=&quot;10&quot; users=&quot;23&quot; voices=&quot;0&quot; webcams=&quot;0&quot; /&gt; 
 *       [ Other lobby entries ]
 *     &lt;/room&gt;
 *     [ Other public rooms ]
 *     &lt;room
 *         type=&quot;user&quot; 
 *         id=&quot;1600004725&quot; 
 *         name=&quot;hassansaeed87&amp;aposs room&quot; 
 *         topic=&quot;Welcome to My Room&quot;&gt;
 *       &lt;lobby count=&quot;1&quot; users=&quot;1&quot; voices=&quot;0&quot; webcams=&quot;0&quot; /&gt;
 *     &lt;/room&gt; 
 *     [ Other private rooms ]
 *   &lt;/chatRooms&gt;
 * &lt;/content&gt;
 * </pre>
 * 
 * NOTE: the XML reader used in this code is very simplistic. As the format
 * employed by Yahoo is quite simple, I've choosen to implement my own reader
 * rather than rely on the industrial-strength readers which are available for
 * later versions of Java. This keeps the resource footprint of the API small
 * and maitains accessiblity to early/embedded versions of Java. The reader is
 * certainly *not* a full (or correct) XML parser, and may break if the file
 * format changes radically.
 * 
 * NOTE: this class used to rely on a home-brew HTTP class called
 * openymsg.network.HTTPConnection . This was because Yahoo had a nasty habit of
 * sending back HTTP responses with no blank line between the header and the
 * opening XML line (invalid, in other words!) As Sept 2006 (v0.7) the problem
 * appears to have been fixed, so this code has been converted to use the
 * regular java.net.* HTTP classes.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YahooChatCategoryV1 implements YahooChatCategory<YahooChatRoomV1, YahooChatCategoryV1>{
	private final static String PUBLIC_TYPE = "yahoo";

	private final static String CAT_URL = "insider.msg.yahoo.com/ycontent/?chatroom_";

	private final String name;

	private final long id;

	private final String cookieLine; // Cookie HTTP header

	private final Set<YahooChatCategoryV1> subcategories;

	private final Set<YahooChatRoomV1> privateRooms;

	private final Set<YahooChatRoomV1> publicRooms;

	private final String localePrefix;

	/**
	 * Chatroom lobbies hashed by network name;
	 */
	private final Hashtable<String, YahooChatLobbyV1> chatByNetName;

	/**
	 * Creates a new Category instance.
	 * 
	 * @param id
	 * @param rawName
	 * @param cookieLine
	 * @param localePrefix
	 */
	public YahooChatCategoryV1(long id, String rawName, String cookieLine,
			String localePrefix) {
		this.id = id;
		name = Util.entityDecode(rawName);
		this.cookieLine = cookieLine;
		this.localePrefix = localePrefix;

		subcategories = new HashSet<YahooChatCategoryV1>();
		privateRooms = new HashSet<YahooChatRoomV1>();
		publicRooms = new HashSet<YahooChatRoomV1>();
		chatByNetName = new Hashtable<String, YahooChatLobbyV1>();
	}

	/**
	 * Adds a new subcategory to this category.
	 * 
	 * @param category
	 *            the new subcategory of this category.
	 */
	public void addSubcategory(YahooChatCategoryV1 category) {
		subcategories.add(category);
	}

	/**
	 * Returns all public rooms in this category. This excludes the rooms from
	 * subcategories of this object.
	 * 
	 * @return All public rooms.
	 */
	public Set<YahooChatRoomV1> getPublicRooms() {
		return publicRooms;
	}

	/**
	 * Returns all private rooms in this category. This excludes the rooms from
	 * subcategories of this object.
	 * 
	 * @return All private rooms.
	 */
	public Set<YahooChatRoomV1> getPrivateRooms() {
		return privateRooms;
	}

	/**
	 * Returns all subcategories of this category.
	 * 
	 * @return All subcategories.
	 */
	public Set<YahooChatCategoryV1> getSubcategories() {
		return subcategories;
	}

	/**
	 * Returns the 'pretty-printed' name for this category.
	 * 
	 * @return Category name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a numberic category identifier.
	 * 
	 * @return Category identifier.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Package level methods: get lobby object based upon network name
	 */
	public YahooChatLobbyV1 getLobby(String networkName) {
		return chatByNetName.get(networkName);
	}

	/**
	 * Object as text string
	 */
	@Override
	public String toString() {
		return "YahooChatCategory [name=" + name + " id=" + id + ']';
	}

	/**
	 * The loadRooms() method loads both private and public rooms for this
	 * category. The first time a category is inspected, we need to fetch the
	 * data from Yahoo to populate it. If this method is called again, a
	 * complete new tree of categories, rooms and lobbies will be created.
	 * 
	 * @throws IOException
	 */
	/*
	 * TODO: recreation should be made impossible by adding this code to the
	 * constructor. Instead of recreation, event handling should properly modify
	 * the existing tree.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void loadRooms() throws Exception {
		publicRooms.clear();
		privateRooms.clear();

		// Open a HTTP connection to a given category
		final String addr = ChatroomManager.PREFIX + localePrefix + CAT_URL;
		final URLConnection uConn = new URL(addr + id).openConnection();
		Util.initURLConnection(uConn);

		if (cookieLine != null) {
			uConn.setRequestProperty("Cookie", cookieLine);
		}

		final SAXBuilder builder = new SAXBuilder();
		final Document doc = builder.build(new URL(addr + id));

		final Element chatRooms = doc.getRootElement().getChild("chatRooms");

		final List<Element> rooms = chatRooms.getChildren("room");
		for (Element room : rooms) {
			final String roomType = room.getAttributeValue("type");
			final long roomId = Long.parseLong(room.getAttributeValue("id"));
			final String roomName = room.getAttributeValue("name");
			final String roomTopic = room.getAttributeValue("topic");

			final YahooChatRoomV1 yahooChatRoom = new YahooChatRoomV1(roomId,
					roomName, roomTopic, roomType.equals(PUBLIC_TYPE));

			final List<Element> lobbies = room.getChildren("lobby");
			for (Element lobby : lobbies) {
				final int lobbyNumber = Integer.parseInt(lobby
						.getAttributeValue("count"));
				final int reportedUsers = Integer.parseInt(lobby
						.getAttributeValue("users", "-1"));
				final int reportedVoices = Integer.parseInt(lobby
						.getAttributeValue("voices", "-1"));
				final int reportedWebcams = Integer.parseInt(lobby
						.getAttributeValue("webcams", "-1"));

				final YahooChatLobbyV1 l = yahooChatRoom.createLobby(lobbyNumber);
				l.setReportedUsers(reportedUsers);
				l.setReportedVoices(reportedVoices);
				l.setReportedWebcams(reportedWebcams);

				// Hash on room:lobby, so we can find it in chat packet code
				chatByNetName.put(l.getNetworkName(), l);

			}
			if (yahooChatRoom.isPublic()) {
				publicRooms.add(yahooChatRoom);
			} else {
				privateRooms.add(yahooChatRoom);
			}

		}
	}
}
