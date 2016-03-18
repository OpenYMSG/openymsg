/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol. Copyright (C) 2007 G. der Kinderen,
 * Nimbuzz.com This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openymsg.legacy.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A YMSG9 packet has a 20 byte fixed format header. The first four bytes are the magic code "YMSG". The next four
 * contain the protocol version 0x09000000. The next two are the size in bytes of the body. Following this a two byte
 * service id (which effectively determines the message/body type). Then a four byte status code, usually zero on
 * outgoing packets. Finally a four byte session code, which should always parrot in outgoing packets the last incoming
 * packet (it can switch mid-session, apparently!) The body is a set of ASCII strings terninated by the byte sequence
 * 0xc080 . Some of these strings are 'instructions' written as numbers, which appear to dictate the type and/or meaning
 * of the data which follows in the next section. In a way they are like key/value pairs, except that not all keys
 * appear to require values. (Given the user- unfriendly nature of these codes, and their apparent inconsistent usage
 * from one message type to another, little reverse engineered information exists as to their actual meaning. Those
 * implementing the YMSG9 protocol tend to just copy them verbatim.)
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YMSG9InputStream extends BufferedInputStream {
	private static final Log log = LogFactory.getLog(YMSG9InputStream.class);

	public YMSG9InputStream(InputStream in) {
		super(in);
	}

	/**
	 * Read a complete packet, including headers. Returns null upon EOF, and throws IOException when confused.
	 */
	public YMSG9Packet readPacket() throws IOException {
		YMSG9Packet p = new YMSG9Packet();
		String charEncoding = System.getProperty("openymsg.network.charEncoding", "UTF-8");
		byte[] header = new byte[20];
		if (readBuffer(header) <= 0)
			return null;
		// Somewhat ineligant way to extract the header data
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 4; i++)
			sb.append((char) header[i]);
		p.magic = sb.toString();
		p.version = u2i(header[5]);
		p.length = (u2i(header[8]) << 8) + (u2i(header[9]));
		p.service = ServiceType.getServiceType((u2i(header[10]) << 8) + (u2i(header[11])));
		p.status = (u2i(header[12]) << 24) + (u2i(header[13]) << 16) + (u2i(header[14]) << 8) + (u2i(header[15]));
		p.sessionId = (u2i(header[16]) << 24) + (u2i(header[17]) << 16) + (u2i(header[18]) << 8) + (u2i(header[19]));
		// Check the header
		if (!p.magic.equals("YMSG"))
			throw new IOException("Bad YMSG9 header");
		// Read the body
		List<String> v = new ArrayList<String>();
		byte[] body = new byte[p.length];
		if (readBuffer(body) < 0)
			return null;
		// Now extract strings in the body
		int start = 0;
		boolean keyPos = true;
		for (int i = 0; i < body.length - 1; i++) {
			if (u2i(body[i]) == 0xc0 && u2i(body[i + 1]) == 0x80) {
				// Create a encoded string (eg. UTF-8) and add to array
				String s = new String(body, start, i - start, charEncoding);
				if (keyPos) {
					s = cleanse(s);
					if (isKey(s))
						v.add(s);
				} else
					v.add(s);
				keyPos = !keyPos;
				// Skip over second byte in separator, reset string start
				i++;
				start = i + 1;
			}
		}
		// There is an issue with Yahoo adding a terminating 0xc0 0x80 0c00
		// onto the end of some chat packets. This creates a key '0'
		// without a value. So, check for odd size and delete last index.
		if ((v.size() % 2) != 0)
			v.remove(v.size() - 1);
		// Convert the collection into an array
		p.body = new String[v.size()];
		p.body = v.toArray(p.body);
		log.debug(p.toString());
		if (p.service == null)
			throw new UnknowServiceException(p);
		return p;
	}

	private int u2i(byte b) {
		return b & 0xff;
	}

	/**
	 * I've noticed on some chat rooms, particularly those with lots of users which require multiple packets to send the
	 * membership list, the second packet can contain garbled data, usually false elements beginning with a zero byte
	 * (\0). I've no idea if this is a bug in Yahoo's code, a deliberate attempt to make third party clients crash, or
	 * merely a bug in what *I've* written. This code is an attempt to trap and deal with this troublesome data. If
	 * anyone has any better ideas, get in touch!
	 */
	private String cleanse(String s) {
		// First off, some extended chat login messages are garbled.
		// Ditching zero characters seems to work (?)
		while (s.length() > 0 && (s.charAt(0) == 0 || s.charAt(0) > 0x7f)) {
			s = s.substring(1);
		}
		return s;
	}

	private boolean isKey(String s) {
		// If there are non digit characters, we've failed
		for (int i = 0; i < s.length(); i++)
			if (!Character.isDigit(s.charAt(i)))
				return false;
		// If the number is too big, we've failed
		return (s.length() <= 5);
	}

	/**
	 * Reads an entire buffer of data, allowing for blocking. Returns bytes read (should be == to buffer size) or
	 * negative bytes read if 'EOF' encountered.
	 * @param buff Buffer to fill with data.
	 * @return Bytes read
	 */
	private int readBuffer(byte[] buff) throws IOException {
		int p = 0, r = 0;
		while (p < buff.length) {
			r = super.read(buff, p, buff.length - p);
			if (r < 0)
				return (p + 1) * -1;
			p += r;
		}
		return p;
	}
}
