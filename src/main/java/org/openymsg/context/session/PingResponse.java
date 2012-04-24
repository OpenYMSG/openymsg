package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

// As we're sending pings back, it's probably safe to ignore the
// incoming pings from Yahoo.
public class PingResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(PingResponse.class);

	@Override
	public void execute(YMSG9Packet packet) {
		log.debug("Received PING (but ignoring it).");
		String value143 = packet.getValue("143");
		if (value143 == null || value143.isEmpty()) {
			log.warn("No 143 value for Ping");
		}
		else if (!value143.equals("60")) {
			log.warn("143 value for Ping is not 60: " + value143);
		}
		String value144 = packet.getValue("144");
		if (value144 == null || value144.isEmpty()) {
			log.warn("No 144 value for Ping");
		}
		else if (!value144.equals("1")) {
			log.warn("144 value for Ping is not 1: " + value143);
		}
	}

}
