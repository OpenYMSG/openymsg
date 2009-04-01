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

import java.net.URL;

/**
 * Represents a LoginRefused exception, caused by a locked account on the Yahoo
 * network.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class AccountLockedException extends LoginRefusedException {
	private static final long serialVersionUID = -7278019920906316416L;

	private URL location;

	/**
	 * Creates a new instance of the Exception, indicating that the account that
	 * was used to login to the Yahoo network is Locked by Yahoo administrators.
	 * 
	 * @param message
	 *            Description of this exception.
	 * @param url
	 *            URL of a webpage where more information can be found.
	 */
	public AccountLockedException(String message, URL url) {
		super(message, AuthenticationState.LOCKED);
		location = url;
	}

	/**
	 * Returns the URL that is included in the Exception. It should give you
	 * further information.
	 * 
	 * @return The URL of a website.
	 */
	public URL getWebPage() {
		return location;
	}
}
