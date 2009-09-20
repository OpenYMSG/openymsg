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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Many (most?) other YMSG library developers refer to this code as part of the
 * v11 protocol. The reason for this seems to be that this code first became
 * active while eleven was the latest version. However, Yahoo servers seem to
 * support at least the two most recent protocol versions, meaning that this
 * code was actually first added into their v10 clients, but not activated
 * immediately. Activation came much later, when version nine was retired, by
 * which time version eleven was already on the scene.
 * 
 * I think it is more correct to label this challenge/response code as version
 * ten. (The current v11 probably has some hidden gems which won't be activated
 * until twelve is with us, and ten is finally end-of-line'd!) If you study
 * other YMSG libraries you should be aware of the labeling differences.
 * 
 * Yahoo has repeatedly tweaked their login protocol, by designing the
 * challenge/response code in such a way that aspects of the algorithm will
 * remain unused until the challenge key is tweaked to cause them to swing into
 * effect. The major change came around September 2003, when Yahoo dropped
 * support for version nine clients, allowing them to switch on a few nasties
 * they had added into the version ten c/r code. Then in January 2004 they
 * switched on a new way of encoding their messages using a fake mathematical
 * expression. Again in June 2004 they tweaked the c/r to take advantage of a
 * flaw in the way many third party libraries had implemented the algorithm
 * (most of whom base their code on work done by the Gaim and Trillian
 * projects).
 * 
 * Yahoo claim that they do these things to protect their users from spam and
 * the like - when will they learn that third party developers are (almost
 * always) not the enemy...? They don't like spammers any more than Yahoo do
 * (indeed many of them have added advanced filtering and security technologies
 * into their clients above-and-beyond that which Yahoo supports). If only they
 * would stop wasting so much time fighting third party developers, and
 * co-operate with them, so together we can devise ways of shutting out spammers
 * for good!
 * 
 * Here endeth the rant ;-)
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class ChallengeResponseV10 extends ChallengeResponseUtility implements
		ChallengeResponseV10Tables {
	/**
	 * These lookup tables are used in decoding the challenge string.
	 */
	private final static String ALPHANUM_LOOKUP = "qzec2tb3um1olpar8whx4dfgijknsvy5"; 
	private final static String OPERATORS_LOOKUP = "+|&%/*^-";

	/**
	 * These lookup tables are used in encoding the response strings
	 */
	private final static String ENCODE1_LOOKUP = "FBZDWAGHrJTLMNOPpRSKUVEXYChImkwQ"; 
	private final static String ENCODE2_LOOKUP = "F0E1D2C3B4A59687abcdefghijklmnop"; 
	private final static String ENCODE3_LOOKUP = ",;";

	private final static String BINARY_DATA = "/challenge.bin";

	// Buffer for binary data
	private static byte[] data = null;

	/**
	 * Given a username, password and challenge string, this code returns the
	 * two valid response strings needed to login to Yahoo.
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
	public static String[] getStrings(final String username,
			final String password, final String challenge)
			throws NoSuchAlgorithmException, IOException {
		int operand = 0, i;

		// Count the number of operator characters, as this determines the
		// size of our magic bytes array
		int cnt = 0;
		for (i = 0; i < challenge.length(); i++) {
			if (isOperator(challenge.charAt(i))) {
				cnt++;
			}
		}

		int[] magic = new int[cnt];

		// PART ONE : Store operands, and OR them with operators.
		// (Note: ignore brackets, they are just there to confuse - making
		// the challenge string look like an expression of some sort!)
		cnt = 0;
		for (i = 0; i < challenge.length(); i++) {
			char c = challenge.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c)) {
				// 0-31, shifted to high 5 bits
				operand = ALPHANUM_LOOKUP.indexOf(c) << 3; 
			} else if (isOperator(c)) {
				// 0-7
				int a = OPERATORS_LOOKUP.indexOf(c);
				// Mask with operand
				magic[cnt] = (operand | a) & 0xff; 
				cnt++;
			}
		}

		// PART TWO : Monkey around with the data
		for (i = magic.length - 2; i >= 0; i--) {
			int a = magic[i];
			int b = magic[i + 1];
			a = ((a * 0xcd) ^ b) & 0xff;
			magic[i + 1] = a;
		}

		// PART THREE : Create 20 byte buffer, copy first 4 bytes into arrays
		byte[] comparison = _part3Munge(magic); // 20 bytes
		long seed = 0; // First 4 bytes reversed
		byte[] binLookup = new byte[7]; // First 4 plus 3 empty
		for (i = 0; i < 4; i++) {
			seed = seed << 8;
			seed += (comparison[3 - i] & 0xff);
			binLookup[i] = (byte) (comparison[i] & 0xff);
		}

		// PART THREE AND A BIT : Binary table lookup params
		int table = 0, depth = 0;
		// Because this code may block for some time, we won't use
		// the thread safe md5() we inherited from our super class.
		// Instead we'll create and instance local to this thread.
		MessageDigest localMd5 = MessageDigest.getInstance("MD5");
		for (i = 0; i < 0xffff; i++) {
			for (int j = 0; j < 5; j++) {
				binLookup[4] = (byte) (i & 0xff);
				binLookup[5] = (byte) ((i >> 8) & 0xff);
				binLookup[6] = (byte) j;
				localMd5.reset();
				byte[] result = localMd5.digest(binLookup);
				if (_part3Compare(result, comparison) == true) {
					depth = i;
					table = j;
					i = 0xffff;
					j = 5; // Exit loops
				}
			}
		}

		// PART THREE AND A BIT MORE : Do binary table lookup
		byte[] magicValue = new byte[4];
		seed = _part3Lookup(table, depth, seed); // Check PART THREE for
		seed = _part3Lookup(table, depth, seed); // seed generation
		for (i = 0; i < magicValue.length; i++) {
			magicValue[i] = (byte) (seed & 0xff);
			seed = seed >> 8;
		}

		// PART FOUR : This bit is copied from the start of V9
		String regular = yahoo64(md5(password));
		String crypted = yahoo64(md5(md5Crypt(password, "$1$_2S43d5f")));
		// And now for some more hashing, this time with SHA-1
		boolean hackSha1 = (table >= 3); // June 2004
		String[] s = new String[2];
		s[0] = _part4Encode(_part4Hash(regular, magicValue, hackSha1));
		s[1] = _part4Encode(_part4Hash(crypted, magicValue, hackSha1));

		return s;
	}

	private static byte[] _part3Munge(int[] magic) {
		int res, i = 1;
		byte[] comparison = new byte[20];
		// Add two bytes at a time to fill up array
		for (int c = 0; c < comparison.length; c += 2) {
			int a, b;
			a = magic[i++];
			if (a <= 0x7f) // Bit 8 set?
			{
				res = a;
			} else
			// Bit 8 unset?
			{
				// Munge data: offset and offset+1
				if (a >= 0xe0) // >=224? (%11100000)
				{
					b = magic[i++]; // Next byte from 'magic'
					a = (a & 0x0f) << 6; // Bits 10-7 from low 4 bits of
					// 'a'
					b = b & 0x3f; // Bits 6-1 from low 6 bits of 'b'
					res = (a + b) << 6; // Combine/shift: aaaabbbbbb000000
				} else {
					res = (a & 0x1f) << 6; // Shift: 0000aaaaaa000000
				}
				// Munge data: result and next 'magic' byte
				res += (magic[i++] & 0x3f);
			}
			// Bits 16-1 are places into next two array slots
			comparison[c] = (byte) ((res & 0xff00) >> 8); // 16-9 bits to
			// [a+0]
			comparison[c + 1] = (byte) (res & 0xff); // 8-1 bits to
			// [a+1];
		}

		return comparison;
	}

	private static int _part3Lookup(int table, int depth, long seed)
			throws IOException {
		int offset = 0; // Offset into data table
		long a, b, c; // Temp variables

		long idx = seed; // Choose table entry (unsigned int)
		int iseed = (int) seed; // 32 bit *signed*
		for (int i = 0; i < depth; i++) {
			// Table 0 (full of IDENT no-ops) deleted to save space
			if (table == 0)
				return iseed;
			// Get op from table (idx is an 'unsigned int', so adjust if
			// necessary!)
			if (idx < 0)
				idx += 0x100000000L; // More sign bit to bit #32
			int[] opArr = OPS[table][(int) (idx % 96)];

			switch (opArr[OP]) {
			// case IDENT : // Removed: see the 'if'
			// return iseed; // condition at top of loop
			case XOR:
				// Bitwise XOR
				iseed ^= opArr[ARG1];
				break;
			case MULADD:
				// Multiply and add
				iseed = iseed * opArr[ARG1] + opArr[ARG2];
				break;
			case LOOKUP:
				// Arg 1 determines which table in the binary data
				offset = TABLE_OFFSETS[opArr[ARG1]];
				// Replace each of the four seed bytes with the byte
				// at that offset in the 256 byte table of arg1
				b = _data(offset, (iseed & 0xff))
						| _data(offset, (iseed >> 8) & 0xff) << 8
						| _data(offset, (iseed >> 16) & 0xff) << 16
						| _data(offset, (iseed >> 24) & 0xff) << 24;
				iseed = (int) b;
				break;
			case BITFLD:
				// Arg 1 determines which table in the binary data
				offset = TABLE_OFFSETS[opArr[ARG1]];
				c = 0;
				// All 32 bytes in table
				for (int j = 0; j < 32; j++) {
					// Move j'th bit to position data[j];
					a = ((iseed >> j) & 1) << _data(offset, j);
					// Mask out data[j]'th bit
					b = ~(1 << _data(offset, j)) & c;
					// Combine
					c = a | b;
				}
				iseed = (int) c;
				break;
			}
			// Last run of the loop? Don't do final part!
			if (depth - i <= 1)
				return iseed;

			// Bit more mesing about with the seed and table index before
			// we loop again. Mess about with each byte in copy of seed
			// to get new idx, then scale up the seed.
			a = 0;
			c = iseed;
			for (int j = 0; j < 4; j++) {
				a = (a ^ c & 0xff) * 0x9e3779b1;
				c = c >> 8;
			}
			idx = (int) ((((a ^ (a >> 8)) >> 16) ^ a) ^ (a >> 8)) & 0xff;
			iseed = iseed * 0x00010dcd;
		}
		// Should return inside loop before we reach this point
		return iseed;
	}

	/**
	 * Returns the requested data, loading the data file if it wasn't loaded
	 * before.
	 * 
	 * @param offset
	 * @param idx
	 * @return
	 * @throws IOException
	 */
	private final static int _data(int offset, int idx) throws IOException {
		if (data == null) {
			loadData();
		}

		return (data[offset + idx] & 0xff);
	}

	/**
	 * Replaces the 'data' field with the data found in BINARY_DATA
	 * 
	 * @throws IOException
	 */
	private static final void loadData() throws IOException {
		final int size = TABLE_OFFSETS[TABLE_OFFSETS.length - 1];

		final InputStream stream;
		if ((stream = ChallengeResponseV10.class
				.getResourceAsStream(BINARY_DATA)) == null) {
			throw new IOException("BINARY_DATA does not seem to exist ["
					+ BINARY_DATA + "]");
		}

		data = new byte[size];

		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(stream);
			if (is.read(data) != size) {
				throw new IllegalStateException("Data too short?");
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private final static boolean _part3Compare(byte[] a, byte[] b) {
		for (int i = 0; i < 16; i++) {
			if (a[i] != b[i + 4]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Each 16 bit value (2 bytes) is encoded into three chars, 5 bits per char,
	 * with the least sig bit either ',' or ';' - a equals is inserted in the
	 * middle to make it look like an assignment.
	 */
	private static String _part4Encode(byte[] buffer) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length; i += 2) {
			// 16 bit value from bytes i and i+1
			int a = ((buffer[i] & 0xff) << 8) + (buffer[i + 1] & 0xff);
			// Bits 16 to 12 from first table, followed by a '='
			sb.append(ENCODE1_LOOKUP.charAt((a >> 11) & 0x1f));
			sb.append('=');
			// Bits 11 to 7, then 6 to 2, from second table
			sb.append(ENCODE2_LOOKUP.charAt((a >> 6) & 0x1f));
			sb.append(ENCODE2_LOOKUP.charAt((a >> 1) & 0x1f));
			// Bit 1 is taken from third table
			sb.append(ENCODE3_LOOKUP.charAt(a & 0x01));
		}
		return sb.toString();
	}

	private static byte[] _part4Hash(String target, byte[] magicValue,
			boolean hackSha1) {
		// Convert string to 64 byte arrays with padding
		byte[] xor1 = _part4Xor(target, 0x36);
		byte[] xor2 = _part4Xor(target, 0x5c);

		// Hash with SHA-1, first hash feeds into second
		SHA1 sha1 = new SHA1();
		sha1.update(xor1);
		if (hackSha1)
			sha1.setBitCount(0x1ff); // 24th June 2004 'mod'
		sha1.update(magicValue);
		byte[] digest1 = sha1.digest();
		sha1.reset();
		sha1.update(xor2);
		sha1.update(digest1);
		byte[] digest2 = sha1.digest();
		return digest2;
	}

	private static byte[] _part4Xor(String s, int op) {
		byte[] arr = new byte[64];
		// XOR against op
		for (int i = 0; i < s.length(); i++)
			arr[i] = (byte) (s.charAt(i) ^ op);
		// Pad remainder of 64 bytes with op
		for (int i = s.length(); i < arr.length; i++)
			arr[i] = (byte) op;
		return arr;
	}

	/**
	 * Checks if the character is one of the eight operator characters.
	 * 
	 * @param c
	 * @return ''true'' if c is one of the eight operator chars, ''false''
	 *         otherwise.
	 */
	private static boolean isOperator(char c) {
		return (OPERATORS_LOOKUP.indexOf(c) >= 0);
	}
}
