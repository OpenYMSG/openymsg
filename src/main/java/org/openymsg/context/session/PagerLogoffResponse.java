package org.openymsg.context.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.contact.SessionContactImpl;
import org.openymsg.context.SessionContextImpl;
import org.openymsg.network.YMSG9Packet;

// TODO - connection isn't closed yet
// TODO - really a Pager Log off
/**
 * Process an incoming AUTHRESP packet. If we get one of these it means the logon process has failed. Set the session
 * state to be failed, and flag the end of login. Note: we don't throw exceptions on the input thread, but instead we
 * pass them to the thread which called login()
 */
public class PagerLogoffResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(PagerLogoffResponse.class);
	private final String username;
	private final SessionContextImpl sessionContext;
	private final SessionContactImpl sessionContact;

	public PagerLogoffResponse(String username, SessionContextImpl sessionContext, SessionContactImpl sessionContact) {
		this.username = username;
		this.sessionContext = sessionContext;
		this.sessionContact = sessionContact;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		log.trace("Received Pager Logoff packet.");
		String packetUser = packet.getValue("7");
		// TODO handle protocol
		if (username.equalsIgnoreCase(packetUser) || packetUser == null) {
			handleMyLogoff(packet);
		} else {
			log.warn("Got a logoff for another user: " + packet);
			YahooContact contact = new YahooContact(packetUser, YahooProtocol.YAHOO);
			sessionContact.receivedContactLogoff(contact);
		}
	}

	private void handleMyLogoff(YMSG9Packet packet) {
		LogoutReason state = null;
		if (packet.exists("66")) {
			long l = Long.parseLong(packet.getValue("66"));
			try {
				state = LogoutReason.getStatus(l);
			} catch (Exception e1) {
				log.warn("AUTHRESP says: logged out without an unknown reason: " + l);
			}
		} else {
			log.info("AUTHRESP says: logged off without a reason" + LogoutReason.NO_REASON);
		}
		sessionContext.receivedLogout(state);
	}
}
