package org.openymsg;

import java.util.StringTokenizer;

import org.openymsg.network.YMSG9Packet;

public class PacketReader {
	private static final String TEST = "Magic:YMSG Version:16 Length:187 Service:STATUS_15 Status:DEFAULT SessionId:0x479a43  [241] [0] [302] [315] [300] [315] [7] [neiliihart] [10] [0] [13] [1] [192] [1142663985] [198] [0] [213] [2] [244] [12582847] [300] [444] [440] [0] [301] [444] [550] [ERGV732G4XICQINSM7YBVG3AEQ] [301] [315] [303] [315]";

	public static final void main(String[] args) {
		StringTokenizer tokenizer = new StringTokenizer(TEST, " ");
		String magic = tokenizer.nextToken().replaceAll("Magic:", "");
		String version = tokenizer.nextToken().replaceAll("Version:", "");
		String length = tokenizer.nextToken().replaceAll("Length:", "");
		String service = tokenizer.nextToken().replaceAll("Service:", "");
		String status = tokenizer.nextToken().replaceAll("Status:", "");
		String sessionId = tokenizer.nextToken().replaceAll("SessionId:", "");
		System.err.println("start: " + magic + "/" + version + "/" + length + "/" + service + "/" + status + "/" + sessionId);
		YMSG9Packet packet = new YMSG9Packet();
		packet.magic = magic;
		System.err.println(packet);
//		packet.s

		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();

			System.err.println("value: " + nextToken);
		}
	}
}
