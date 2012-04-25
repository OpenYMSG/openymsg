package org.openymsg.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class NewMailResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(NewMailResponse.class);
	
	@Override
	public void execute(YMSG9Packet packet) {
		String count = packet.getValue("9");
		log.info("new mail count: " + count);
	}

}
