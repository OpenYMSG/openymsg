package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

//TODO - connection isn't closed yet
//TODO - really a Pager Log off
/**
 * Process an incoming AUTHRESP packet. If we get one of these it means the logon process has failed. Set the session
 * state to be failed, and flag the end of login. Note: we don't throw exceptions on the input thread, but instead we
 * pass them to the thread which called login()
 */
public class PagerLogoffResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(PagerLogoffResponse.class);
	private final String username;
	private final SessionSessionImpl session;

	public PagerLogoffResponse(String username, SessionSessionImpl session) {
		this.username = username;
		this.session = session;
	}

	@Override
	public void execute(YMSG9Packet packet) {
		log.trace("Received Pager Logoff packet.");
		String packetUser = packet.getValue("7");
		if (username.equalsIgnoreCase(packetUser)) {
			handleMyLogoff(packet);
		} else {
			log.warn("Got a logoff for another user: " + packet);
		}

	}

	private void handleMyLogoff(YMSG9Packet packet) {
		LogoutReason state = null;
		if (packet.exists("66")) {
			long l = Long.parseLong(packet.getValue("66"));
			try {
				state = LogoutReason.getStatus(l);
			}
			catch (Exception e1) {
				log.warn("AUTHRESP says: logged out without an unknown reason: " + l);
			}
		} else {
			log.info("AUTHRESP says: logged off without a reason" + LogoutReason.NO_REASON);
		}
		session.receivedLogout(state);
	}

}
