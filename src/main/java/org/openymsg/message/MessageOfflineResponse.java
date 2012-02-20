package org.openymsg.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponseAbstract;

public class MessageOfflineResponse extends SinglePacketResponseAbstract {
	private static final Log log = LogFactory.getLog(MessageOfflineResponse.class);
	private SessionMessageImpl session;

	public MessageOfflineResponse(SessionMessageImpl session) {
		this.session = session;
	}

	@Override
	protected void execute() {
		int i = 0;
		// Read each message, until null
		while (this.packet.getNthValue("31", i) != null) {
			extractOfflineMessage(i);
			i++;
		}
	}

	private void extractOfflineMessage(int i) {
		// TODO handle identities?
		// final String to = this.packet.getNthValue("5", i);
		final String from = this.packet.getNthValue("4", i);
		final String message = this.packet.getNthValue("14", i);
		final String timestamp = this.packet.getNthValue("15", i);

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
