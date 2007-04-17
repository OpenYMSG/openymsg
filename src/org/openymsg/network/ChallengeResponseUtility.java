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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
class ChallengeResponseUtility {
	private final static String Y64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz" + "0123456789._";

	/**
	 * Yahoo uses its own custom variation on Base64 encoding (although a little
	 * birdy tells me this routine actually comes from the Apple Mac?)
	 * 
	 * For those not familiar with Base64 etc, all this does is treat an array
	 * of bytes as a bit stream, sectioning the stream up into six bit slices,
	 * which can be represented by the 64 characters in the 'table' Y64 above.
	 * In this fashion raw binary data can be expressed as valid 7 bit printable
	 * ASCII - although the size of the data will expand by 25% - three bytes
	 * (24 bits) taking up four ASCII characters. Now obviously the bit stream
	 * will terminate mid way throught an ASCII character if the input array
	 * size isn't evenly divisible by 3. To flag this, either one or two dashes
	 * are appended to the output. A single dash if we're two over, and two
	 * dashes if we're only one over. (No dashes are appended if the input size
	 * evenly divides by 3.)
	 */
	final static String yahoo64(byte[] buffer) {
		int limit = buffer.length - (buffer.length % 3);
		// int pos = 0;
		StringBuffer out = new StringBuffer();
		int[] buff = new int[buffer.length];

		for (int i = 0; i < buffer.length; i++)
			buff[i] = buffer[i] & 0xff;

		for (int i = 0; i < limit; i += 3) {
			// Top 6 bits of first byte
			out.append(Y64.charAt(buff[i] >> 2));
			// Bottom 2 bits of first byte append to top 4 bits of second
			out
					.append(Y64.charAt(((buff[i] << 4) & 0x30)
							| (buff[i + 1] >> 4)));
			// Bottom 4 bits of second byte appended to top 2 bits of third
			out.append(Y64.charAt(((buff[i + 1] << 2) & 0x3c)
					| (buff[i + 2] >> 6)));
			// Bottom six bits of third byte
			out.append(Y64.charAt(buff[i + 2] & 0x3f));
		}

		// Do we still have a remaining 1 or 2 bytes left?
		int i = limit;
		switch (buff.length - i) {
		case 1:
			// Top 6 bits of first byte
			out.append(Y64.charAt(buff[i] >> 2));
			// Bottom 2 bits of first byte
			out.append(Y64.charAt(((buff[i] << 4) & 0x30)));
			out.append("--");
			break;
		case 2:
			// Top 6 bits of first byte
			out.append(Y64.charAt(buff[i] >> 2));
			// Bottom 2 bits of first byte append to top 4 bits of second
			out
					.append(Y64.charAt(((buff[i] << 4) & 0x30)
							| (buff[i + 1] >> 4)));
			// Bottom 4 bits of second byte
			out.append(Y64.charAt(((buff[i + 1] << 2) & 0x3c)));
			out.append("-");
			break;
		}

		return out.toString();
	}

	/**
	 * Return the MD5 or a string and byte array.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	final static byte[] md5(String s) throws NoSuchAlgorithmException {
		return md5(s.getBytes());
	}

	final synchronized static byte[] md5(byte[] buff)
			throws NoSuchAlgorithmException {
		MessageDigest md5Obj = MessageDigest.getInstance("MD5");
		md5Obj.reset();
		return md5Obj.digest(buff);
	}

	/**
	 * Return the MD5Crypt of a string and salt
	 */
	final static byte[] md5Crypt(String k, String s)
			throws NoSuchAlgorithmException {
		return UnixMD5Crypt.crypt(k, s).getBytes();
	}
}
