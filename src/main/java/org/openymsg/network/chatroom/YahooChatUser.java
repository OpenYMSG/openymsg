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

import org.openymsg.network.YahooUser;


/**
 * This class wraps a regular YahooUser to provide the extra information
 * provided for each chat user.
 * 
 * Note: if a YahooUser object for this user does not exist, one is
 * automatically created and added to the static users hash in YahooUser.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface YahooChatUser extends YahooUser // Cannot be serialised
{

	boolean isMale();

	boolean isFemale();

	boolean hasWebcam();

	/**
	 * @return the age
	 */
	int getAge();

	/**
	 * @return the alias
	 */
	String getAlias();


	/**
	 * @return the attributes
	 */
	int getAttributes();


	/**
	 * @return the location
	 */
	String getLocation();

}
