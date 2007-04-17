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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public abstract class Util implements PropertyConstants {
	// Read openymsg.network.loginTimeout, or use default
	public static int loginTimeout(int def) {
		int loginTimeout = Integer.parseInt(System.getProperty(
				"openymsg.network.loginTimeout", "" + def)) * 1000;
		if (loginTimeout <= 0)
			loginTimeout = Integer.MAX_VALUE;
		return loginTimeout;
	}

	// Read openymsg.network.directHost, or use default
	public static String directHost() {
		return System.getProperty(DIRECT_HOST, DIRECT_HOST_DEFAULT);
	}

	// Read openymsg.network.directHosts, or use default
	static int[] directPorts() {
		String s = System.getProperty(DIRECT_PORTS);
		if (s == null) {
			return DIRECT_PORTS_DEFAULT;
		}
		StringTokenizer st = new StringTokenizer(s, ",");
		int[] arr = new int[st.countTokens()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = Integer.parseInt(st.nextToken());
		return arr;
	}

	// Read openymsg.network.directHosts[0], or use default
	public static int directPort() {
		return directPorts()[0];
	}

	// Read openymsg.network.httpHost, or use default
	public static String httpHost() {
		return System.getProperty(HTTP_HOST, HTTP_HOST_DEFAULT);
	}

	// Read openymsg.network.httpHost, or use default
	public static String httpProxyAuth() {
		return System.getProperty(HTTP_PROXY_AUTH);
	}

	// Read openymsg.network.httpHost, or use default
	public static String fileTransferHost() {
		return System.getProperty(FT_HOST, FT_HOST_DEFAULT);
	}

	/**
	 * HTTP
	 */
	// Read http.proxyHost, or proxyHost, or null
	public static String httpProxyHost() {
		String host = System.getProperty(NetworkConstants.PROXY_HOST);
		if (host == null)
			host = System.getProperty(NetworkConstants.PROXY_HOST_OLD);
		return host;
	}

	// Read http.proxyPort, or proxyPort, or return 8080
	public static int httpProxyPort() {
		String port = System.getProperty(NetworkConstants.PROXY_PORT);
		if (port == null)
			port = System.getProperty(NetworkConstants.PROXY_PORT_OLD);
		if (port == null)
			port = "8080";
		return Integer.parseInt(port);
	}

	public static void initURLConnection(URLConnection uc) {
		// Sets the auth for proxies
		String proxyAuthContent = httpProxyAuth();
		if (proxyAuthContent != null)
			uc.setRequestProperty("Proxy-Authorization", proxyAuthContent);
	}

	/*
	 * public static String getPropString(String key,String def) { return
	 * System.getProperty(key,def); }
	 * 
	 * public static int getPropInt(String key,int def) { String v =
	 * System.getProperty(key); if(v==null) return def; try { return
	 * Integer.parseInt(v); } catch(NumberFormatException e) { return def; } }
	 * 
	 * public static int[] getPropIntA(String key,int[] def) { String v =
	 * System.getProperty(key); if(v==null) return def; StringTokenizer st = new
	 * StringTokenizer(v,","); int[] arr = new int[st.countTokens()]; int cnt=0;
	 * while(st.hasMoreTokens()) { try {
	 * arr[cnt++]=Integer.parseInt(st.nextToken()); }
	 * catch(NumberFormatException e) {} } return arr; }
	 */

	/**
	 * For those not familiar with Base64 etc, all this does is treat an array
	 * of bytes as a bit stream, sectioning the stream up into six bit slices,
	 * which can be represented by the 64 characters in the 'table' provided. In
	 * this fashion raw binary data can be expressed as valid 7 bit printable
	 * ASCII - although the size of the data will expand by 25% - three bytes
	 * (24 bits) taking up four ASCII characters. Now obviously the bit stream
	 * will terminate mid way throught an ASCII character if the input array
	 * size isn't evenly divisible by 3. To flag this, either one or two pad
	 * chars are appended to the output. A single char if we're two over, and
	 * two chars if we're only one over. (No chars are appended if the input
	 * size evenly divides by 3.)
	 */
	public static String _base64(String table, char pad, byte[] buffer) {
		int limit = buffer.length - (buffer.length % 3);
		StringBuffer out = new StringBuffer();

		// Convert bytes to ints, for convenience
		int[] buff = new int[buffer.length];
		for (int i = 0; i < buffer.length; i++)
			buff[i] = buffer[i] & 0xff;

		// Base 64
		for (int i = 0; i < limit; i += 3) {
			// Top 6 bits of first byte
			out.append(table.charAt(buff[i] >> 2));
			// Bottom 2 bits of first byte append to top 4 bits of second
			out.append(table.charAt(((buff[i] << 4) & 0x30)
					| (buff[i + 1] >> 4)));
			// Bottom 4 bits of second byte appended to top 2 bits of third
			out.append(table.charAt(((buff[i + 1] << 2) & 0x3c)
					| (buff[i + 2] >> 6)));
			// Bottom six bits of third byte
			out.append(table.charAt(buff[i + 2] & 0x3f));
		}

		// Do we still have a remaining 1 or 2 bytes left?
		int i = limit;
		switch (buff.length - i) {
		case 1:
			// Top 6 bits of first byte
			out.append(table.charAt(buff[i] >> 2));
			// Bottom 2 bits of first byte
			out.append(table.charAt(((buff[i] << 4) & 0x30)));
			out.append(pad).append(pad);
			break;
		case 2:
			// Top 6 bits of first byte
			out.append(table.charAt(buff[i] >> 2));
			// Bottom 2 bits of first byte append to top 4 bits of second
			out.append(table.charAt(((buff[i] << 4) & 0x30)
					| (buff[i + 1] >> 4)));
			// Bottom 4 bits of second byte
			out.append(table.charAt(((buff[i + 1] << 2) & 0x3c)));
			out.append(pad);
			break;
		}

		return out.toString();
	}

	public static String base64(byte[] buffer) {
		return _base64("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz" + "0123456789+/", '=', buffer);
	}

	/**
	 * Is Utf-8 text
	 */
	public final static boolean isUtf8(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) > 0x7f)
				return true;
		}
		return false;
	}

	/**
	 * Decode entity encodeded strings
	 */
	private final static String[] ENTITIES_STR = { "apos", "quot", "amp", "lt",
			"gt", "nbsp", "curren", "cent", "pound", "yen", "copy" };

	private final static char[] ENTITIES_CHR = { '\'', '\"', '&', '<', '>',
			' ', 164, 162, 163, 165, 169 };

	public static String entityDecode(String s) {

		StringBuffer result = new StringBuffer();

		int i1 = s.indexOf("&"), i2;
		while (i1 >= 0) {
			i2 = s.indexOf(";");
			if (i2 >= 0 && i2 > i1 + 1) // Found the sequence & followed by ;
			{
				result.append(s.substring(0, i1));
				String ent = s.substring(i1 + 1, i2).toLowerCase();
				int j = 0;
				for (j = 0; j < ENTITIES_STR.length; j++) {
					// Entity matched
					if (ENTITIES_STR[j].equals(ent)) {
						result.append(ENTITIES_CHR[j]);
						break;
					}
				}
				// Entity unmatched
				if (j >= ENTITIES_STR.length) {
					result.append('&');
					result.append(ent);
					result.append(';');
				}
				// Truncate ip buffer
				s = s.substring(i2 + 1);
			} else
			// Found &, but no *following* ;
			{
				result.append(s.substring(0, i1 + 1));
				s = s.substring(i1 + 1);
			}
			i1 = s.indexOf("&");
		}
		result.append(s);
		return result.toString();
	}

	/**
	 * Revert a base64 (yahoo64) encoded string back to its original unsigned
	 * byte data.
	 */
	public static int[] yahoo64Decode(String s) {
		if (s.length() % 4 != 0)
			throw new IllegalArgumentException("Source string incomplete");

		// Figure out the correct length for byte buffer
		int len = s.length() / 4;
		if (s.endsWith("--"))
			len -= 2;
		else if (s.endsWith("-"))
			len--;

		int[] buffer = new int[len];
		int[] c = new int[4];
		int bpos = 0;

		// For data streams which were not exactly divisible by three
		// bytes, the below will result in an exception for the padding
		// chars on the end of the string.
		try {
			for (int i = 0; i < s.length(); i += 4) {
				for (int j = 0; i < c.length; j++)
					c[j] = _c2b(s.charAt(i + j));
				buffer[bpos + 0] = ((c[0] << 2) + (c[1] >> 4)) & 0xff;
				buffer[bpos + 1] = ((c[1] & 0x0f) << 4 + (c[2] >> 2)) & 0xff;
				buffer[bpos + 2] = ((c[2] & 0x03) << 6 + (c[3])) & 0xff;
				bpos += 3;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return buffer;
	}

	private static int _c2b(int c) {
		if (c >= 'A' && c <= 'Z')
			return c - 'A';
		else if (c >= 'a' && c <= 'z')
			return c - 'a' + 26;
		else if (c >= '0' && c <= '9')
			return c - '0' + 52;
		else if (c == '.')
			return 62;
		else if (c == '_')
			return 63;
		else
			return 0;
	}

	/**
	 * Returns the contents of the file in a byte array. (The Java Developers
	 * Almanac 1.4)
	 * 
	 * @param file
	 *            The file to read.
	 * @return An array of all bytes from the file.
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		final InputStream is = new FileInputStream(file);

		// Get the size of the file
		final long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		final byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * Returns the value found for 'key' in the hashtable. If no such key
	 * exists, the default value is returned.
	 * 
	 * @param table
	 *            The hashtable to look in for 'key'.
	 * @param key
	 *            The key for which the value should be returned.
	 * @param defaultValue
	 *            Value to return if 'key' doesn't exist in 'table'.
	 * @return The value for 'key' in 'table', or 'defaultValue' if it 'key'
	 *         doesn't exist in 'table'.
	 */
	public static String getValue(final Hashtable<String, String> table,
			final String key, final String defaultValue) {
		if (table.containsKey(key)) {
			return table.get(key);
		}

		return defaultValue;
	}

}
