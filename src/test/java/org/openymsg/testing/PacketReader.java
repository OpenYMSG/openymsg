package org.openymsg.testing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openymsg.network.MessageStatus;
import org.openymsg.network.ServiceType;
import org.openymsg.network.YMSG9Packet;

public class PacketReader {
	private static final String TEST = "Magic:YMSG Version:16 Length:187 Service:STATUS_15 Status:DEFAULT SessionId:0x479a43  [241] [0] [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [192] [1142663985] [198] [0] [213] [2] [244] [12582847] [300] [444] [440] [0] [301] [444] [550] [ERGV732G4XICQINSM7YBVG3AEQ] [301] [315] [303] [315]";

	@SuppressWarnings("unused")
	public static YMSG9Packet readString(String string) {
		StringTokenizer tokenizer = new StringTokenizer(string, " ");
		String magic = tokenizer.nextToken().replaceAll("Magic:", "");
		String version = tokenizer.nextToken().replaceAll("Version:", "");
		String length = tokenizer.nextToken().replaceAll("Length:", "");
		String service = tokenizer.nextToken().replaceAll("Service:", "");
		String status = tokenizer.nextToken().replaceAll("Status:", "");
		String sessionId = tokenizer.nextToken().replaceAll("SessionId:", "");
		// System.err.println("start: " + magic + "/" + version + "/" + length + "/" + service + "/" + status + "/"
		// + sessionId);
		YMSG9Packet packet = new YMSG9Packet();
		packet.magic = magic;
		packet.length = Integer.valueOf(length);
		packet.service = ServiceType.valueOf(service);
		// packet.sessionId = sessionId;
		// packet.version = version;
		packet.status = MessageStatus.valueOf(status).getValue();

		List<String> elements = new ArrayList<String>();
		String nextFullEntry = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String nextEntry = token;
			boolean foundEnd = false;
			if (nextEntry.startsWith("[")) {
				nextEntry = nextEntry.substring(1, nextEntry.length());
			} else {
				nextEntry = " " + nextEntry;
			}

			if (nextEntry.endsWith("]")) {
				nextEntry = nextEntry.substring(0, nextEntry.length() - 1);
				foundEnd = true;
			}
			nextFullEntry = nextFullEntry + nextEntry;

			// String key = tokenizer.nextToken().replace("[", "").replace("]", "");
			if (foundEnd) {
				// System.err.println("adding: " + nextFullEntry);
				elements.add(nextFullEntry);
				nextFullEntry = "";
			}
		}

		packet.body = elements.toArray(new String[0]);
		return packet;

	}

	public static final void main(String[] args) throws UnsupportedEncodingException, IOException {
		YMSG9Packet packet = readString(TEST);
		System.err.println(packet);

	}
}
