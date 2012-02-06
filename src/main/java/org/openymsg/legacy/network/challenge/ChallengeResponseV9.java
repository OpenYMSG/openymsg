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
package org.openymsg.legacy.network.challenge;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class ChallengeResponseV9 extends ChallengeResponseUtility {
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
     * Given a username, password and challenge string, this code returns the two valid response strings needed to login
     * to Yahoo.
     * 
     * @param username
     *            Username of the session that is trying to authenticate
     * @param password
     *            Password that validates <tt>username</tt>
     * @param challenge
     *            The challenge as received from the Yahoo network.
     * @return The two valid response Strings needed to finish authenticating.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String[] getStrings(final String username, final String password, final String challenge)
            throws NoSuchAlgorithmException {

        final String[] s = new String[2];
        s[0] = yahoo64(md5(password));
        s[1] = yahoo64(md5(md5Crypt(password, "$1$_2S43d5f")));

        // combination mode
        int mode = challenge.charAt(15) % 8;

        // The mode determines the 'checksum' character
        final char checksum = challenge.charAt(challenge.charAt(CHECKSUM_POS[mode]) % 16);

        // Depending upon the mode, the various strings are combined
        // differently
        s[0] = yahoo64(md5(checksum + combine(username, s[0], challenge, mode)));
        s[1] = yahoo64(md5(checksum + combine(username, s[1], challenge, mode)));

        return s;
    }

    /**
     * The 'mode' (see getStrings() above) determines the order the various strings and the hashed/encrypted password
     * are concatenated. For efficiency I stuff all the values into an array and use a table to determine the order they
     * should be glued together.
     * 
     * @param username
     *            Username String
     * @param password
     *            Password String
     * @param challenge
     *            Challenge String
     * @param mode
     *            Combination mode identifier
     * @return combination of username, password and challenge, combined as suggested by the 'mode' parameter.
     */
    private static String combine(final String username, final String password, final String challenge, final int mode) {
        final StringBuilder sb = new StringBuilder();
        final String[] sa = { username, password, challenge };
        for (int i = 0; i < 3; i++) {
            sb.append(sa[STRING_ORDER[mode][i]]);
        }
        return sb.toString();
    }
}
