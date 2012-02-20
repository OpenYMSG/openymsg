package org.openymsg.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponseAbstract;
import org.openymsg.network.YMSG9Packet;

/**
 * Process an incoming AUTH packet (in response to the AUTH packet we transmitted to the server).
 * <p>
 * Format: <tt>"1" <loginID> "94" <challenge string * (24 chars)></tt>
 * <p>
 * Old Note: Only a check is done now.
 * for YMSG10 and later, Yahoo sneakily changed the challenge/response method dependent upon a switch in field
 * '13'. If this field is absent or 0, use v9, if 1 or 2, then use v10.
 */
public class LoginInitResponse extends SinglePacketResponseAbstract{
	private static final Log log = LogFactory.getLog(LoginInitResponse.class);
	private final SessionAuthorizeImpl sessionAuthorize;

	public LoginInitResponse(SessionAuthorizeImpl sessionAuthorize) {
		this.sessionAuthorize = sessionAuthorize;
	}

	@Override
	public void execute() {
		this.receiveAuth(this.packet);
	}

	protected void receiveAuth(YMSG9Packet pkt) {// 0x57
		//TODO - get the message out of sync
//		if (sessionStatus != SessionState.CONNECTING) {
//			throw new IllegalStateException("Received a response to an AUTH packet, outside the normal"
//					+ " login flow. Current state: " + sessionStatus);
//		}
		log.trace("Received AUTH from server. Going to parse challenge...");
		// Value for key 13: '0'=v9, '1'=v10, '2'=v16
		String version = pkt.getValue("13");
		if (version == null || !version.equals("2")) {
			log.debug("Auth version is not 2: " + version);
		}
		String seed = pkt.getValue("94");
		sessionAuthorize.setSeed(seed);
	}

}
