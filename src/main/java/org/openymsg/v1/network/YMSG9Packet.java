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
package org.openymsg.v1.network;

import java.util.ArrayList;
import java.util.List;

import org.openymsg.network.ServiceType;

/**
 * This class is nothing more than a convenient data structure to hold the
 * information extracted from a single YMSG packet (message). The body array
 * holds the list of strings, in sequence, as they appeared in the body section
 * of the packet.
 * 
 * This class is returned by YMSG9InputStreamReader.readPacket(); See
 * YMSG9InputStream.java for more details on the protocol.
 * 
 * Note: the term 'packet' here is strictly speaking incorrect, as a YMSG
 * message could in theory take up more than one TCP packet - but it helps to
 * distinguish these lower-level network messages from the higher-level dialogue
 * 'message's in the protocol.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YMSG9Packet {
	public String magic; // Header 1

	public int version, length; // Header 2

	public ServiceType service;

	public long status, sessionId; // Header 3

	public String[] body; // Packet data body

	String quickSetAccessSeparator; // String used to break sets

	int[] quickSetAccess = null; // Speeds multi-set access

	/**
	 * General body accessors
	 */

	// Returns the *key index* (not value index) of n'th field of type k
	private int getNthLocation(String k, int n) {
		for (int i = 0; i < body.length; i += 2) {
			if (body[i].equals(k))
				n--;
			if (n < 0)
				return i;
		}
		return -1;
	}

	// Returns n'th value of field of type k
	String getNthValue(String k, int n) {
		int l = getNthLocation(k, n);
		if (l < 0)
			return null;
		return body[l + 1];
	}

	// Returns the first value of field of type k
	String getValue(String k) {
		return getNthValue(k, 0);
	}

	// Returns a subset array with only values of field type k
	String[] getValues(String k) {
		int cnt = 0, j = 0;
		for (int i = 0; i < body.length; i += 2)
			if (body[i].equals(k))
				cnt++;
		String[] sa = new String[cnt];
		for (int i = 0; i < body.length; i += 2)
			if (body[i].equals(k))
				sa[j++] = body[i + 1];
		return sa;
	}

	// Returns the value for field type k, in the n'th set beginning with 'set'
	String getValueFromNthSet(String set, String k, int n) {
		int i = getNthLocation(set, n);
		if (i < 0)
			return null;
		i += 2;
		while (i < body.length) {
			if (body[i].equals(k))
				return body[i + 1]; // Found it
			else if (body[i].equals(set))
				return null; // Start of next set
			else
				i += 2;
		}
		return null;
	}

	boolean exists(String k) {
		return (getValue(k) != null);
	}

	/**
	 * Quick access accessors, for working with bodies of multiple records
	 * (sets). The method generateQuickSetAccessors() must be called before any
	 * accessor method will work.
	 */

	// Several packet types contain repeating groups of data, for example
	// multiple lists of friends. Each 'record' in the body is signified
	// by a standard field (a 'separator') which always (?) appears as the
	// first field - for example in the case of friends updates, the field
	// 109 (username) denotes a new record.
	//
	// This method creates a 2D array of start/end positions for each record.
	//
	// separator: the separator field type
	void generateQuickSetAccessors(String separator) {
		// Already generated?
		if (quickSetAccess != null && quickSetAccessSeparator.equals(separator))
			return;

		quickSetAccessSeparator = separator;
		// Count ahead, to work out how big our array must be
		int cnt = 0;
		for (int i = 0; i < body.length; i += 2)
			if (body[i].equals(separator))
				cnt++;
		// Create array
		quickSetAccess = new int[cnt + 1];
		// Populate array
		int pos = 0, i;
		for (i = 0; i < cnt; i++) {
			// Wind forward to find start of record, and store location.
			while (pos < body.length && !body[pos].equals(separator))
				pos += 2;
			quickSetAccess[i] = pos;
			pos += 2;
		}
		// Mark end
		quickSetAccess[i] = body.length;
	}

	// Returns the value for field type k, in the n'th set beginning with 'set'
	String getValueFromNthSetQA(String k, int n) {
		for (int i = quickSetAccess[n]; i < quickSetAccess[n + 1]; i += 2) {
			if (body[i].equals(k))
				return body[i + 1];
		}
		return null;
	}

	boolean existsSetQA(String k, int n) {
		return (getValueFromNthSetQA(k, n) != null);
	}

	/**
	 * Other stuff
	 */

	// FIX: Not thread safe (reading array while copies are taking place)
	void append(YMSG9Packet pkt) {
		String[] arr = new String[body.length + pkt.body.length];
		System.arraycopy(body, 0, arr, 0, body.length);
		System.arraycopy(pkt.body, 0, arr, body.length, pkt.body.length);
		body = arr;
	}

	// Merge the supplied packet into this one, with special regard to
	// certain fields which need to be concatenated. If the field key is
	// in the array provided, it is appended onto the end of the current value
	// for that key (if it doesn't exist it is created). Other keys are
	// appended to the end of this packet.
	// FIX: Not thread safe (reading array while copies are taking place)
	void merge(YMSG9Packet pkt, String[] concatFields) {
		List<String> appendBuffer = new ArrayList<String>();

		for (int i = 0; i < pkt.body.length; i += 2) {
			// Get next key/value
			String k = pkt.body[i], v = pkt.body[i + 1];
			// Look for key in list of merging fields
			boolean b = false;
			for (int j = 0; j < concatFields.length; j++)
				if (concatFields[j].equals(k)) {
					b = true;
					break;
				}
			// Its on the list, so attempt to merge to current
			if (b) {
				int idx = getNthLocation(k, 0);
				// If may be on the list, but do we have a field like this
				// already?
				if (idx < 0) {
					// No! Forget about the merge, just append to list
					// FIX: what happens if two such fields appear in the same
					// packet - one will overwrite the other? (Can this happen?)
					appendBuffer.add(k);
					appendBuffer.add(pkt.body[i + 1]);
				} else {
					// Yes! Merge!
					body[idx + 1] = body[idx + 1] + v;
				}
			} else {
				// Append new body field to current body field
				appendBuffer.add(k);
				appendBuffer.add(pkt.body[i + 1]);
			}
		}
		if (appendBuffer.size() > 0) {
			String[] arr = new String[body.length + appendBuffer.size()];
			System.arraycopy(body, 0, arr, 0, body.length);
			for (int i = 0; i < appendBuffer.size(); i++)
				arr[body.length + i] = appendBuffer.get(i);
			body = arr;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Magic:").append(magic);
		sb.append(" Version:").append(version);
		sb.append(" Length:").append(length);
		sb.append(" Service:").append(service);
		sb.append(" Status:").append(status);
		sb.append(" SessionId:0x").append(Long.toHexString(sessionId));
		sb.append(" ");
		for (int i = 0; i < body.length; i++)
			sb.append(" [" + body[i] + "]");
		if (quickSetAccess != null) {
			sb.append(" ").append(quickSetAccessSeparator).append(":");
			for (int i = 0; i < quickSetAccess.length; i++)
				sb.append(quickSetAccess[i]).append(" ");
		}
		return sb.toString();
	}
}
