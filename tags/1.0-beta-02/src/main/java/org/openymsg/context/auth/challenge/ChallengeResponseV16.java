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
package org.openymsg.context.auth.challenge;

import java.security.*;

/**
 * @author Damian Minkov
 */
public class ChallengeResponseV16 extends ChallengeResponseUtility implements ChallengeResponseStrategy {
	// -----------------------------------------------------------------
	// Given a username, password and challenge string, this code returns
	// the two valid response strings needed to login to Yahoo
	// -----------------------------------------------------------------
	public String getStrings(String challenge) throws NoSuchAlgorithmException {
		byte[] md5_digest = md5(challenge);
		String base64_string = yahoo64(md5_digest);
		return base64_string;
	}
}
