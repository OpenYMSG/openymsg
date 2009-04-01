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
package org.openymsg.network.challenge;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 * Note that this UNIX CRYPT implementation is a partial implementation only, as
 * it supports no other methods than MD5Crypt
 * 
 * Originally converted by the folks at www.orangefood.com
 * 
 * This class implements the popular MD5Crypt function as used by BSD and most
 * modern Un*x systems. It was basically converted from the C code write by
 * Poul-Henning Kamp. Here is his comment:
 * 
 * "THE BEER-WARE LICENSE" (Revision 42): <phk@login.dknet.dk> wrote this file.
 * As long as you retain this notice you can do whatever you want with this
 * stuff. If we meet some day, and you think this stuff is worth it, you can buy
 * me a beer in return. Poul-Henning Kamp
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author Poul-Henning Kamp, phk@login.dknet.dk
 * @author S.E. Morris
 */
public final class UnixCrypt {
	private final static String MAGIC = "$1$";

	private final static byte[] ITOA64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
			.getBytes();

	private final static void to64(final StringBuilder sb, int n, int nCount) {
		while (--nCount >= 0) {
			sb.append((char) ITOA64[n & 0x3f]);
			n >>= 6;
		}
	}

	/**
	 * Partial implementation of Unix CRYPT. This implementation supports
	 * MD5Crypt only.
	 * 
	 * (partial copy of <tt>man CRYPT(3)<tt>)
	 * 
	 * The crypt() function performs password encryption based on the NBS Data
	 * Encryption Standard (DES). Additional code has been added to deter key
	 * search attempts and to use stronger hashing algorithms.
	 * 
	 * The first argument to crypt() is a NUL-terminated string, typically a us-
	 * er's typed password. The second is in one of three forms: if it begins
	 * with an underscore (`_') then an extended format is used in interpreting
	 * both the key and the setting, as outlined below. If it begins with a
	 * string character (`$') and a number then a different algorithm is used
	 * depending on the number. At the moment a `$1' chooses MD5 hashing and a
	 * `$2' chooses Blowfish hashing; see below for more information.
	 * 
	 * (...)
	 * 
	 * MD5 crypt
	 * 
	 * For MD5 crypt the version number, salt and the hashed password are 
	 * separated by the `$' character.  The maximum length of a password is 
	 * limited by the length counter of the MD5 context, which is about 2**64.
	 * A valid MD5 password entry looks like this: 
	 * <tt>$1$caeiHQwX$hsKqOjrFRRN6K32OWkCBf1</tt>.
	 * 
	 * The whole MD5 password string is passed as setting for interpretation.
	 * 
	 * @param key 
	 *            typically a user's typed password.
	 * @param salt 
	 *            The salt for this crypt, prepended by <tt>$1</tt> as 
	 *            this method supports MD5 hashing only.
	 * @return The Crypted representation of the 'key' argument.
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalArgumentException if the 'salt' argument does not
	 *             start with <tt>$1</tt> (which is the indication that
	 *             MD5 should be used).
	 */
	public static String crypt(final String key, final String salt)
			throws NoSuchAlgorithmException {
		if (key == null || key.length() == 0) {
			throw new IllegalArgumentException(
					"Argument 'key' cannot be null or an empty String.");
		}

		if (salt == null || salt.length() == 0) {
			throw new IllegalArgumentException(
					"Argument 'salt' cannot be null or an empty String.");
		}
		final StringTokenizer st = new StringTokenizer(salt, "$");

		// The first part of the salt is assumed to be '1', as that is the CRYPT
		// identifier for MD5 hashing
		final String method = st.nextToken();

		if ("1".equals(method)) {
			return md5Crypt(key, st.nextToken());
		}

		throw new IllegalArgumentException("Unsupported crypt method "
				+ "(this implementation only supports md5Crypt). Make sure"
				+ " your salt starts with $1.");
	}

	/**
	 * Implementation of Unix MD5Crypt.
	 * 
	 * @param key
	 *            The to-be-crypted string, usually a password
	 * @param salt
	 *            The salt for the crypt (without a prepending <tt>$1$</tt>
	 *            string).
	 * @return The MD5Crypted representation of the 'key' argument.
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5Crypt(final String key, final String salt)
			throws NoSuchAlgorithmException {
		if (key == null || key.length() == 0) {
			throw new IllegalArgumentException(
					"Argument 'key' cannot be null or an empty String.");
		}

		if (salt == null || salt.length() == 0) {
			throw new IllegalArgumentException(
					"Argument 'salt' cannot be null or an empty String.");
		}

		return md5Crypt(key.getBytes(), salt.getBytes());
	}

	/**
	 * Implementation of Unix MD5Crypt.
	 * 
	 * @param key
	 *            The to-be-crypted byte array, usually a password
	 *            representation
	 * @param salt
	 *            The salt for the crypt (without a prepending <tt>$1$</tt>
	 *            bytes).
	 * @return The MD5Crypted representation of the 'crypteMe' argument.
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5Crypt(final byte[] key, final byte[] salt)
			throws NoSuchAlgorithmException {
		if (key == null || key.length == 0) {
			throw new IllegalArgumentException(
					"Argument 'key' cannot be null or an empty array.");
		}

		if (salt == null || salt.length == 0) {
			throw new IllegalArgumentException(
					"Argument 'salt' cannot be null or an empty array.");
		}
		final MessageDigest _md = MessageDigest.getInstance("MD5");

		_md.update(key);
		_md.update(MAGIC.getBytes());
		_md.update(salt);

		final MessageDigest md2 = MessageDigest.getInstance("MD5");
		md2.update(key);
		md2.update(salt);
		md2.update(key);

		byte[] abyFinal = md2.digest();

		for (int n = key.length; n > 0; n -= 16) {
			_md.update(abyFinal, 0, n > 16 ? 16 : n);
		}
		abyFinal = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		// "Something really weird"
		// Not sure why 'j' is here as it is always zero, but it's in Kamp's
		// code too
		for (int j = 0, i = key.length; i != 0; i >>>= 1) {
			if ((i & 1) == 1)
				_md.update(abyFinal, j, 1);
			else
				_md.update(key, j, 1);
		}

		// Build the output string
		final StringBuilder sbPasswd = new StringBuilder();
		sbPasswd.append(MAGIC);
		sbPasswd.append(new String(salt));
		sbPasswd.append('$');

		abyFinal = _md.digest();

		// And now, just to make sure things don't run too fast
		// in C . . . "On a 60 Mhz Pentium this takes 34 msec, so you would
		// need 30 seconds to build a 1000 entry dictionary..."
		for (int n = 0; n < 1000; n++) {
			final MessageDigest md3 = MessageDigest.getInstance("MD5");
			// MD5Init(&ctx1);
			if ((n & 1) != 0) {
				md3.update(key);
			} else {
				md3.update(abyFinal);
			}

			if ((n % 3) != 0) {
				md3.update(salt);
			}
			if ((n % 7) != 0) {
				md3.update(key);
			}

			if ((n & 1) != 0) {
				md3.update(abyFinal);
			} else {
				md3.update(key);
			}

			abyFinal = md3.digest();
		}

		// Convert to int's so we can do our bit manipulation
		// it's a bit tricky making the byte act unsigned
		int[] anFinal = new int[] {
				(abyFinal[0] & 0x7f) | (abyFinal[0] & 0x80),
				(abyFinal[1] & 0x7f) | (abyFinal[1] & 0x80),
				(abyFinal[2] & 0x7f) | (abyFinal[2] & 0x80),
				(abyFinal[3] & 0x7f) | (abyFinal[3] & 0x80),
				(abyFinal[4] & 0x7f) | (abyFinal[4] & 0x80),
				(abyFinal[5] & 0x7f) | (abyFinal[5] & 0x80),
				(abyFinal[6] & 0x7f) | (abyFinal[6] & 0x80),
				(abyFinal[7] & 0x7f) | (abyFinal[7] & 0x80),
				(abyFinal[8] & 0x7f) | (abyFinal[8] & 0x80),
				(abyFinal[9] & 0x7f) | (abyFinal[9] & 0x80),
				(abyFinal[10] & 0x7f) | (abyFinal[10] & 0x80),
				(abyFinal[11] & 0x7f) | (abyFinal[11] & 0x80),
				(abyFinal[12] & 0x7f) | (abyFinal[12] & 0x80),
				(abyFinal[13] & 0x7f) | (abyFinal[13] & 0x80),
				(abyFinal[14] & 0x7f) | (abyFinal[14] & 0x80),
				(abyFinal[15] & 0x7f) | (abyFinal[15] & 0x80) };

		to64(sbPasswd, anFinal[0] << 16 | anFinal[6] << 8 | anFinal[12], 4);
		to64(sbPasswd, anFinal[1] << 16 | anFinal[7] << 8 | anFinal[13], 4);
		to64(sbPasswd, anFinal[2] << 16 | anFinal[8] << 8 | anFinal[14], 4);
		to64(sbPasswd, anFinal[3] << 16 | anFinal[9] << 8 | anFinal[15], 4);
		to64(sbPasswd, anFinal[4] << 16 | anFinal[10] << 8 | anFinal[5], 4);
		to64(sbPasswd, anFinal[11], 2);

		return sbPasswd.toString();
	}
}
