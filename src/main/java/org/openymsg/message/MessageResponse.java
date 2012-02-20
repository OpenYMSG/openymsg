package org.openymsg.message;

import org.openymsg.Status;
import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming MESSAGE packet. Message can be either online or offline, the latter having a datestamp of when
 * they were sent.
 */
public class MessageResponse implements SinglePacketResponse {
	private MessageOnlineResponse onlineResponse;
	private MessageOfflineResponse offlineResponse;

	public MessageResponse(SessionMessageImpl session) {
		this.onlineResponse = new MessageOnlineResponse(session);
		this.offlineResponse = new MessageOfflineResponse(session);
	}

	@Override
	public void execute(YMSG9Packet packet) {
		if (!packet.exists("14")) {
			// Contains no message?
			return;
		}

		if (packet.status == Status.NOTINOFFICE.getValue()) {
			this.offlineResponse.execute(packet);
		}
		else {
			this.onlineResponse.execute(packet);
		}
	}

}
