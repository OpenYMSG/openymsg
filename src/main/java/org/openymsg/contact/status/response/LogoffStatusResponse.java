package org.openymsg.contact.status.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class LogoffStatusResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(LogoffStatusResponse.class);
	private SingleStatusResponse singleStatusResponse;

	public LogoffStatusResponse(SingleStatusResponse singleStatusResponse) {
		this.singleStatusResponse = singleStatusResponse;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		if (packet.exists("7")) {
			this.singleStatusResponse.execute(packet);
		} else {
			log.warn("Failed handling: " + packet);
		}
	}
}
