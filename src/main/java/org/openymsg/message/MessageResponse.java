package org.openymsg.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming MESSAGE packet. Message can be either online or offline, the latter having a datestamp of when
 * they were sent. Message from MSN have a timestamp.
 */
public class MessageResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(MessageResponse.class);
	private MessageOnlineResponse onlineResponse;
	private MessageOfflineResponse offlineResponse;

	public MessageResponse(SessionMessageImpl session) {
		this.onlineResponse = new MessageOnlineResponse(session);
		this.offlineResponse = new MessageOfflineResponse(session);
	}

	@Override
	public void execute(YMSG9Packet packet) {

		if (packet.status <= 1 || packet.status == MessageStatus.OFFLINE5.getValue()
				|| packet.status == MessageStatus.OFFLINE.getValue()) {
			if (!packet.exists("14")) {
				log.info("Received message with no message");
				return;
			}
			String p2pSessionId = packet.getValue("11");
			if (p2pSessionId != null) {
				log.error("Got p2pSessionId: " + p2pSessionId);
			}
			String imvironment = packet.getValue("63");
			if (imvironment != null && !";0".equals(imvironment)) {
				log.error("Got imvironment: " + imvironment);
			}
			if (packet.status == MessageStatus.OFFLINE5.getValue()) {
				this.offlineResponse.execute(packet);
			}
			else {
				this.onlineResponse.execute(packet);
			}
		}
		else if (packet.status == 2) {
			log.error("Message was not delivered");
		}
	}
}
