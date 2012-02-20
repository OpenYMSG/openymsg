package org.openymsg.auth;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.AuthenticationState;
import org.openymsg.execute.read.SinglePacketResponseAbstract;
import org.openymsg.network.YMSG9Packet;

public class LoginFailureResponse extends SinglePacketResponseAbstract {
	private static final Log log = LogFactory.getLog(LoginFailureResponse.class);
	private final SessionAuthorizeImpl sessionAuthorize;

	public LoginFailureResponse(SessionAuthorizeImpl sessionAuthorize) {
		this.sessionAuthorize = sessionAuthorize;
	}

	@Override
	public void execute() {
		this.receiveAuthResp(this.packet);
	}

	/**
	 * Process an incoming AUTHRESP packet. If we get one of these it means the logon process has failed. Set the
	 * session state to be failed, and flag the end of login. Note: we don't throw exceptions on the input thread, but
	 * instead we pass them to the thread which called login()
	 */
	protected void receiveAuthResp(YMSG9Packet pkt) // 0x54
	{
		log.trace("Received AUTHRESP packet.");
		if (pkt.exists("66")) {
			final long l = Long.parseLong(pkt.getValue("66"));
			switch (AuthenticationState.getStatus(l)) {
			// Account locked out?
			case LOCKED:
				//TODO - handle the url
				@SuppressWarnings("unused")
				URL u;
				try {
					u = new URL(pkt.getValue("20"));
				}
				catch (Exception e) {
					u = null;
				}
				log.info("AUTHRESP says: authentication failed! " + AuthenticationState.LOCKED);
				sessionAuthorize.setState(AuthenticationState.LOCKED);
				break;

			// Bad login (password?)
			case BAD2:
			case BAD:
				log.info("AUTHRESP says: authentication failed! " + AuthenticationState.BAD);
				sessionAuthorize.setState(AuthenticationState.BAD);
				break;

			// unknown account?
			case BADUSERNAME:
				log.info("AUTHRESP says: authentication failed! " + AuthenticationState.BADUSERNAME);
				sessionAuthorize.setState(AuthenticationState.BADUSERNAME);
				break;
			case INVALID_CREDENTIALS:
				log.info("AUTHRESP says: authentication failed! " + AuthenticationState.INVALID_CREDENTIALS);
				sessionAuthorize.setState(AuthenticationState.INVALID_CREDENTIALS);
				break;
			// You have been logged out of the yahoo service, possibly due to a duplicate login.
			case DUPLICATE_LOGIN:
				log.info("AUTHRESP says: Duplicate Login! " + AuthenticationState.DUPLICATE_LOGIN);
				sessionAuthorize.setState(AuthenticationState.DUPLICATE_LOGIN);
				break;
			case UNKNOWN_52:
				log.info("AUTHRESP says: authentication failed with unknown: " + AuthenticationState.UNKNOWN_52);
				sessionAuthorize.setState(AuthenticationState.UNKNOWN_52);
			}
		}
		else {
			log.info("AUTHRESP says: authentication failed without a reason" + AuthenticationState.NO_REASON);
			sessionAuthorize.setState(AuthenticationState.UNKNOWN_52);
		}
	}

}
