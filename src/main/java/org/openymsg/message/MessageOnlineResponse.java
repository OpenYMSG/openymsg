package org.openymsg.message;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class MessageOnlineResponse implements SinglePacketResponse {
	private final static String BUZZ = "<ding>";
	private SessionMessageImpl session;

	public MessageOnlineResponse(SessionMessageImpl session) {
		this.session = session;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		// TODO check for more that one message
		// Sent while we are online
		// TODO - handle indentity
		// final String to = this.packet.getValue("5");
		String from = packet.getValue("4");
		String fed = packet.getValue("241");
		String message = packet.getValue("14");
		String id = packet.getValue("429");
		@SuppressWarnings("unused")
		String timestamp = packet.getValue("15"); // Messages from MSN have timestamp
		@SuppressWarnings("unused")
		String value252 = packet.getValue("252"); // something from MSN
		@SuppressWarnings("unused")
		String value455 = packet.getValue("455"); // something from MSN

		YahooProtocol protocol = YahooProtocol.YAHOO;
		if (fed != null) {
			protocol = YahooProtocol.getProtocolOrDefault(fed, from);
		}
		YahooContact contact = new YahooContact(from, protocol);
		if (message.equalsIgnoreCase(BUZZ)) {
			this.session.receivedBuzz(contact, id);
		} else {
			this.session.receivedMessage(contact, message, id);
		}
	}

}
