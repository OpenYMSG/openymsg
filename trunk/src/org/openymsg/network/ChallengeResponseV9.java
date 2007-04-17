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

import java.security.NoSuchAlgorithmException;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
class ChallengeResponseV9 extends ChallengeResponseUtility {
	final static int[] CHECKSUM_POS = { 7, 9, 15, 1, 3, 7, 9, 15 };

	final static int USERNAME = 0, PASSWORD = 1, CHALLENGE = 2;

	final static int[][] STRING_ORDER = { { PASSWORD, USERNAME, CHALLENGE }, // 0
			{ USERNAME, CHALLENGE, PASSWORD }, // 1
			{ CHALLENGE, PASSWORD, USERNAME }, // 2
			{ USERNAME, PASSWORD, CHALLENGE }, // 3
			{ PASSWORD, CHALLENGE, USERNAME }, // 4
			{ PASSWORD, USERNAME, CHALLENGE }, // 5
			{ USERNAME, CHALLENGE, PASSWORD }, // 6
			{ CHALLENGE, PASSWORD, USERNAME } // 7
	};

	/**
	 * Given a username, password and challenge string, this code returns the
	 * two valid response strings needed to login to Yahoo
	 */
	static String[] getStrings(String username, String password,
			String challenge) throws NoSuchAlgorithmException {
		String[] s = new String[2];
		s[0] = yahoo64(md5(password));
		s[1] = yahoo64(md5(md5Crypt(password, "$1$_2S43d5f")));

		int mode = challenge.charAt(15) % 8;

		// The mode determines the 'checksum' character
		char c = challenge.charAt(challenge.charAt(CHECKSUM_POS[mode]) % 16);

		// Depending upon the mode, the various strings are combined
		// differently
		s[0] = yahoo64(md5(c + combine(username, s[0], challenge, mode)));
		s[1] = yahoo64(md5(c + combine(username, s[1], challenge, mode)));

		return s;
	}

	/**
	 * The 'mode' (see getStrings() above) determines the order the various
	 * strings and the hashed/encyrpted password are concatenated. For
	 * efficiency I stuff all the values into an array and use a table to
	 * determine the order they should be glued together.
	 */
	private static String combine(String u, String p, String c, int mode) {
		StringBuffer sb = new StringBuffer();
		String[] sa = { u, p, c };
		for (int i = 0; i < 3; i++)
			sb.append(sa[STRING_ORDER[mode][i]]);
		return sb.toString();
	}
}
