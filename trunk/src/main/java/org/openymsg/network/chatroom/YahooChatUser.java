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
public class YahooChatUser extends YahooUser // Cannot be serialised
{
	private int age, attributes; // Age and flags

	private String alias, location; // Alias(?) and location

	private final static int MALE_ATTR = 0x08000;

	private final static int FEMALE_ATTR = 0x10000;

	private final static int WEBCAM_ATTR = 0x00010;

	public YahooChatUser(YahooUser yahooUser, int attributes, String alias,
			int age, String location) {
		super(yahooUser.getId());
		this.attributes = attributes;
		this.alias = alias;
		this.age = age;
		this.location = location;
	}

	public boolean isMale() {
		return ((attributes & MALE_ATTR) > 0);
	}

	public boolean isFemale() {
		return ((attributes & FEMALE_ATTR) > 0);
	}

	public boolean hasWebcam() {
		return ((attributes & WEBCAM_ATTR) > 0);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString()).append("] age=")
				.append(age).append(" attributes=").append(
						Integer.toHexString(attributes)).append(" alias=")
				.append(alias).append(" location=").append(location);
		return sb.toString();
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the attributes
	 */
	public int getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(int attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
}
