package org.openymsg.execute.read;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.YMSG9Packet;

public class NoOpResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(NoOpResponse.class);

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		log.debug("handled packet: " + packet);
	}

}
