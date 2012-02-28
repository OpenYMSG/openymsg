package org.openymsg.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

//TODO - connection isn't closed yet
//TODO - really a Pager Log off
/**
 * Process an incoming AUTHRESP packet. If we get one of these it means the logon process has failed. Set the
 * session state to be failed, and flag the end of login. Note: we don't throw exceptions on the input thread, but
 * instead we pass them to the thread which called login()
 */
public class PagerLogoffResponse implements SinglePacketResponse{
	private static final Log log = LogFactory.getLog(PagerLogoffResponse.class);

	@Override
	public void execute(YMSG9Packet packet) {
		log.trace("Received Pager Logoff packet.");
		if (packet.exists("66")) {
			long l = Long.parseLong(packet.getValue("66"));
			LogoutState state = null;
			try {
				state = LogoutState.getStatus(l);
			}
			catch (Exception e1) {
				log.warn ("AUTHRESP says: logged out without an unknown reason: " + l);
			}
			switch (state) {
			case UNKNOWN_52:
				log.info("AUTHRESP says: Logged off with " + state);
//				sessionAuthorize.setState(AuthenticationState.LOCKED);
				break;

			case DUPLICATE_LOGIN1:
			case DUPLICATE_LOGIN2:
				log.info("AUTHRESP says: Logged off with Duplicate Login: " + state);
//				sessionAuthorize.setState(AuthenticationState.LOCKED);
				break;
			default:
				log.warn("AUTHRESP says: logged off with an unchecked reason: " + state);
//				sessionAuthorize.setState(state);
			}
		} else {
				log.info("AUTHRESP says: logged off without a reason" + LogoutState.NO_REASON);
		}
			
	}

}
