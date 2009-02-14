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

import java.util.Set;

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
public interface YahooChatCategory<T extends YahooChatRoom<?>, U extends YahooChatCategory<?,?>> {


	/**
	 * Adds a new subcategory to this category.
	 * 
	 * @param category
	 *            the new subcategory of this category.
	 */
	void addSubcategory(U category);

	/**
	 * Returns all public rooms in this category. This excludes the rooms from
	 * subcategories of this object.
	 * 
	 * @return All public rooms.
	 */
	Set<T> getPublicRooms();

	/**
	 * Returns all private rooms in this category. This excludes the rooms from
	 * subcategories of this object.
	 * 
	 * @return All private rooms.
	 */
	Set<T> getPrivateRooms();

	/**
	 * Returns all subcategories of this category.
	 * 
	 * @return All subcategories.
	 */
	Set<U> getSubcategories();

	/**
	 * Returns the 'pretty-printed' name for this category.
	 * 
	 * @return Category name.
	 */
	String getName();

	/**
	 * Returns a numberic category identifier.
	 * 
	 * @return Category identifier.
	 */
	long getId();

}
