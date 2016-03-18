/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol. Copyright (C) 2007 G. der Kinderen,
 * Nimbuzz.com This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openymsg.legacy.support;

import java.util.Hashtable;

/**
 * Some chatrooms are particularly prone to repeated messages being spammed across the screen - be it by advertisers, or
 * users who selfishly flood the screen with the same text, to get a response!)
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class AntiSpam {
	public static final int REPEAT = 0x0001;
	public static final int FLOOD = 0x0002;
	public static final int CAPS = 0x0004;
	private Hashtable<String, UserHistory> users; // Key=username,
	// value=UserHistory
	private static MessageDecoder decoder; // Decode message to plain text
	// The size of the history used to detect repeat messages
	private static final int MEMORY_SZ = 5;
	// Messages <= this length always pass certain checks
	private static final int MIN_MESG_SZ = 10;

	// FIX: Flood needs implementing!
	// private static final int TRAFFIC_FREQ = 500;
	/**
	 * CONSTRUCTOR
	 */
	public AntiSpam() {
		users = new Hashtable<String, UserHistory>();
	}

	static {
		// As this decoder only decodes to text, we don't need to configure
		// it with MessageDecoderSettings - might as well make it static!
		decoder = new MessageDecoder();
	}

	/**
	 * Accessors
	 */
	public int getViolations(String u, String m) {
		UserHistory h = users.get(u);
		int ret = 0;
		m = decoder.decodeToText(m);
		if (h == null) {
			h = new UserHistory();
			users.put(u, h);
		} else {
			if (isRepeat(h, m))
				ret |= REPEAT;
			if (isFlood(h, m))
				ret |= FLOOD;
			if (isCaps(h, m))
				ret |= CAPS;
		}
		h.update(m, ret);
		return ret;
	}

	/**
	 * Returns true if this user has sent a message identical to this recently (in the last MEMORY_SZ messages).
	 */
	boolean isRepeat(UserHistory h, String m) {
		// Don't check small message (people say "yes" or "ok" a lot)
		if (m.length() <= MIN_MESG_SZ)
			return false;
		// Look for message in history
		for (int i = 0; i < h.memory.length; i++)
			if (h.memory[i] != null && h.memory[i].equals(m))
				return true;
		return false;
	}

	/**
	 * Returns true if the traffic from this user goes over
	 */
	boolean isFlood(UserHistory h,
			String m) { /*
						 * // How long they have been on, in seconds int
						 * timeActive=(int)(System.currentTimeMillis()-startTime)/1000; if( (h.bytesSent+
						 */
		return false;
	}

	boolean isCaps(UserHistory h, String m) {
		// Don't check small messages like "Y" or "OK"
		if (m.length() <= MIN_MESG_SZ)
			return false;
		// Get ratio of high vs. low
		int high = 0, low = 0;
		for (int i = 0; i < m.length(); i++) {
			char c = m.charAt(i);
			if (Character.isLowerCase(c))
				low++;
			else if (Character.isUpperCase(c))
				high++;
		}
		float f;
		if (low > 0)
			f = ((float) high / (float) low) * ((float) 100 / (float) (high + low));
		else
			f = (float) 100.0;
		// Caps outweigh lc 3:1 (75%) or more...?
		return (f >= 75.0);
	}

	/**
	 * Details per user
	 */
	private static class UserHistory {
		String[] memory;
		int memoryPos = 0, bytesSent = 0;

		// long startTime;
		UserHistory() {
			memory = new String[MEMORY_SZ];
			// startTime = System.currentTimeMillis();
		}

		void update(String m, int flags) {
			// Update repeat (only if not already un history!)
			if ((flags & REPEAT) == 0) {
				memory[memoryPos] = m;
				memoryPos++;
				memoryPos %= memory.length;
			}
			// Update flood
			bytesSent += m.length();
		}
	}
}
