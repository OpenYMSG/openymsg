package org.openymsg.contact.status;

import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class LogoffStatusResponse implements SinglePacketResponse {
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
		}
	}

}
