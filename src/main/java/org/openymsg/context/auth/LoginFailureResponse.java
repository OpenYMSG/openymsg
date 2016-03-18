package org.openymsg.context.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

import java.net.URL;

/**
 * Process an incoming AUTHRESP packet. If we get one of these it means the logon process has failed. Set the session
 * state to be failed, and flag the end of login. Note: we don't throw exceptions on the input thread, but instead we
 * pass them to the thread which called login()
 */
public class LoginFailureResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(LoginFailureResponse.class);
	private final SessionAuthenticationImpl sessionAuthorize;

	public LoginFailureResponse(SessionAuthenticationImpl sessionAuthorize) {
		this.sessionAuthorize = sessionAuthorize;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		log.trace("Received AUTHRESP packet.");
		if (packet.exists("66")) {
			long l = Long.parseLong(packet.getValue("66"));
			AuthenticationFailure state = null;
			try {
				state = AuthenticationFailure.getStatus(l);
			} catch (Exception e1) {
				log.warn("AUTHRESP says: authentication  without an unknown reason: " + l);
				sessionAuthorize.setFailureState(AuthenticationFailure.NO_REASON);
				return;
			}
			switch (state) {
				// Account locked out?
				case LOCKED:
					// TODO - handle the url
					URL u = null;
					try {
						u = new URL(packet.getValue("20"));
					} catch (Exception e) {
						u = null;
					}
					log.info("AUTHRESP says: authentication failed with url: " + u + " and: " + state);
					sessionAuthorize.setFailureState(state);
					break;
				// Bad login (password?)
				case BAD2:
				case BAD:
					log.info("AUTHRESP says: authentication failed! " + state);
					sessionAuthorize.setFailureState(state);
					break;
				// unknown account?
				case BADUSERNAME:
					log.info("AUTHRESP says: authentication failed! " + state);
					sessionAuthorize.setFailureState(state);
					break;
				case INVALID_CREDENTIALS:
					log.info("AUTHRESP says: authentication failed! " + state);
					sessionAuthorize.setFailureState(state);
					break;
				default:
					log.warn("AUTHRESP says: authentication without an unchecked reason: " + state);
					sessionAuthorize.setFailureState(state);
			}
		} else {
			log.info("AUTHRESP says: authentication failed without a reason" + AuthenticationFailure.NO_REASON);
			sessionAuthorize.setFailureState(AuthenticationFailure.NO_REASON);
		}
	}
}
