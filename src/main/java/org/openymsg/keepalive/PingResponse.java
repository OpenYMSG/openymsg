package org.openymsg.keepalive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

// As we're sending pings back, it's probably safe to ignore the
// incoming pings from Yahoo.
public class PingResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(PingResponse.class);

	@Override
	public void execute(YMSG9Packet packet) {
		log.debug("Received PING (but ignoring it).");
	}

}
