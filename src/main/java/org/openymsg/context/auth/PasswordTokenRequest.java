package org.openymsg.context.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.url.URLStream;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamStatus;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;

/**
 * Open a HTTP connection with a get token URL with the user's credentials and retrieve authorization and a token.
 * @author neilhart
 */
public class PasswordTokenRequest implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(PasswordTokenRequest.class);
	private final SessionAuthenticationAttemptCallback attemptCallback;
	private SessionConfig config;
	private AuthenticationToken token;

	public PasswordTokenRequest(SessionAuthenticationAttemptCallback attemptCallback, SessionConfig config,
			AuthenticationToken token) {
		this.attemptCallback = attemptCallback;
		this.config = config;
		this.token = token;
	}

	@Override
	public void execute() { // String seed
		String username = token.getUsername();
		String password = token.getPassword();
		String seed = token.getSeed();
		String authLink = config.getPasswordTokenGetUrl(username, password, seed);
		if (authLink == null) {
			log.warn("Failed creating url for: " + username + "/" + password + "/" + seed);
			attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenRequest,
					new AuthenticationAttemptStatusImpl("Failed creating url"));
			return;
		}
		URLStreamBuilder builder = config.getURLStreamBuilder().url(authLink).timeout(config.getConnectionTimeout())
				.disableSSLCheck(config.isSSLCheckDisabled());
		URLStream stream = builder.build();
		URLStreamStatus status = builder.getStatus();
		ByteArrayOutputStream out = stream.getOutputStream();
		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + authLink);
			URLStreamAuthenticationFailureHandler handler =
					new URLStreamAuthenticationFailureHandler(attemptCallback, AuthenticationStep.PasswordTokenRequest);
			status.call(handler);
			return;
		}
		String response = out.toString();
		log.info("Received PasswordTokenResponse: " + response);
		StringTokenizer toks = new StringTokenizer(response, "\r\n");
		if (toks.countTokens() <= 0) {
			String error = "Login Failed, no tokens in response";
			log.warn(error);
			attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenRequest,
					new AuthenticationAttemptStatusImpl(error));
			return;
		}
		Integer responseNo = null;
		try {
			responseNo = Integer.valueOf(toks.nextToken());
		} catch (NumberFormatException e) {
			String error = "Login Failed, wrong response";
			log.warn(error);
			attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenRequest,
					new AuthenticationAttemptStatusImpl(error));
			return;
		}
		if (responseNo != 0 || !toks.hasMoreTokens()) {
			switch (responseNo) {
				case 1235:
					log.info("Login Failed, Invalid username: " + AuthenticationFailure.BADUSERNAME);
					attemptCallback.setFailureState(AuthenticationFailure.BADUSERNAME);
					break;
				case 1212:
					log.info("Login Failed, Wrong password: " + AuthenticationFailure.BAD);
					attemptCallback.setFailureState(AuthenticationFailure.BAD);
					break;
				case 1213:
					log.info("Login locked: Too many failed login attempts: " + AuthenticationFailure.LOCKED);
					attemptCallback.setFailureState(AuthenticationFailure.LOCKED);
					break;
				case 1236:
					log.info("Login locked" + AuthenticationFailure.LOCKED);
					attemptCallback.setFailureState(AuthenticationFailure.LOCKED);
					break;
				case 100:
					log.info("Username or password missing" + AuthenticationFailure.BAD);
					attemptCallback.setFailureState(AuthenticationFailure.BAD);
					break;
				default:
					log.warn("Login Failed, unchecked error: " + responseNo);
					attemptCallback.setFailureState(AuthenticationFailure.NO_REASON);
			}
			return;
		}
		String ymsgr = toks.nextToken();
		if (ymsgr.indexOf("ymsgr=") == -1 && toks.hasMoreTokens()) {
			ymsgr = toks.nextToken();
		}
		ymsgr = ymsgr.replaceAll("ymsgr=", "");
		token.setYmsgr(ymsgr);
		attemptCallback.receivedPasswordToken();
		return;
	}

	@Override
	public void failure(Exception ex) {
		log.error("Failed token request", ex);
		attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenRequest,
				new AuthenticationAttemptStatusImpl("Failed token request" + ex));
	}

	@Override
	public String toString() {
		return "PasswordTokenRequest [token=" + token + "]";
	}
}
