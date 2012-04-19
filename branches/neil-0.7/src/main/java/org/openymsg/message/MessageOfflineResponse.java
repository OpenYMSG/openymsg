package org.openymsg.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class MessageOfflineResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(MessageOfflineResponse.class);
	private SessionMessageImpl session;

	public MessageOfflineResponse(SessionMessageImpl session) {
		this.session = session;
	}

	@Override
	public void execute(YMSG9Packet packet) {
		int i = 0;
		// Read each message, until null
		while (packet.getNthValue("31", i) != null) {
			extractOfflineMessage(i, packet);
			i++;
		}
	}

	private void extractOfflineMessage(int i, YMSG9Packet packet) {
		// TODO handle identities?
		String to = packet.getNthValue("5", i);
		String from = packet.getNthValue("4", i);
		// problem with list
		String fed = packet.getValue("241");
		String message = packet.getNthValue("14", i);
		String timestamp = packet.getNthValue("15", i);
		YahooProtocol protocol = YahooProtocol.YAHOO;
		if (fed != null) {
			protocol = YahooProtocol.getProtocolOrDefault(fed, from);
		}
		YahooContact contact = new YahooContact(from, protocol);

		if (timestamp == null || timestamp.length() == 0) {
			log.warn("Offline message with no timestamp");
			this.session.receivedOfflineMessage(contact, message, 0L);
		} else {
			long timestampInMillis = 1000 * Long.parseLong(timestamp);
			this.session.receivedOfflineMessage(contact, message, timestampInMillis);
		}
	}

}
