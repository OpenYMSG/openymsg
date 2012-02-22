package org.openymsg.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.SinglePacketResponse;
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
		// final String to = this.packet.getNthValue("5", i);
		final String from = packet.getNthValue("4", i);
		final String message = packet.getNthValue("14", i);
		final String timestamp = packet.getNthValue("15", i);

		if (timestamp == null || timestamp.length() == 0) {
			log.warn("Offline message with no timestamp");
			this.session.receivedOfflineMessage(from, message, 0L);
		}
		else {
			long timestampInMillis = 1000 * Long.parseLong(timestamp);
			this.session.receivedOfflineMessage(from, message, timestampInMillis);
		}
	}

}
