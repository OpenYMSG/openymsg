package org.openymsg.auth;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponseAbstract;

/**
 * Process an incoming AUTHRESP packet. If we get one of these it means the logon process has failed. Set the
 * session state to be failed, and flag the end of login. Note: we don't throw exceptions on the input thread, but
 * instead we pass them to the thread which called login()
 */
public class LoginFailureResponse extends SinglePacketResponseAbstract {
	private static final Log log = LogFactory.getLog(LoginFailureResponse.class);
	private final SessionAuthorizeImpl sessionAuthorize;

	public LoginFailureResponse(SessionAuthorizeImpl sessionAuthorize) {
		this.sessionAuthorize = sessionAuthorize;
	}

	@Override
	public void execute() {
		log.trace("Received AUTHRESP packet.");
		if (this.packet.exists("66")) {
			long l = Long.parseLong(this.packet.getValue("66"));
			AuthenticationState state = null;
			try {
				state = AuthenticationState.getStatus(l);
			}
			catch (Exception e1) {
				log.warn ("AUTHRESP says: authentication  without an unknown reason: " + l);
				sessionAuthorize.setState(AuthenticationState.NO_REASON);
			}
			switch (state) {
			// Account locked out?
			case LOCKED:
				//TODO - handle the url
				@SuppressWarnings("unused")
				URL u;
				try {
					u = new URL(this.packet.getValue("20"));
				}
				catch (Exception e) {
					u = null;
				}
				log.info("AUTHRESP says: authentication failed! " + state);
				sessionAuthorize.setState(state);
				break;

			// Bad login (password?)
			case BAD2:
			case BAD:
				log.info("AUTHRESP says: authentication failed! " + state);
				sessionAuthorize.setState(state);
				break;

			// unknown account?
			case BADUSERNAME:
				log.info("AUTHRESP says: authentication failed! " + state);
				sessionAuthorize.setState(state);
				break;
			case INVALID_CREDENTIALS:
				log.info("AUTHRESP says: authentication failed! " + state);
				sessionAuthorize.setState(state);
				break;
			default:
				log.warn("AUTHRESP says: authentication without an unchecked reason: " + state);
				sessionAuthorize.setState(state);
			}
		}
		else {
			log.info("AUTHRESP says: authentication failed without a reason" + AuthenticationState.NO_REASON);
			sessionAuthorize.setState(AuthenticationState.NO_REASON);
		}
	}

}
