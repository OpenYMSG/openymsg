package org.openymsg.auth;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.url.URLStream;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamBuilderImpl;
import org.openymsg.network.url.URLStreamStatus;

/**
 * Open a HTTP connection with a get token URL with the user's credentials and retrieve authorization and a token.
 * @author neilhart
 */
public class PasswordTokenRequest implements Request {
	private static final Log log = LogFactory.getLog(PasswordTokenRequest.class);
	private final SessionAuthenticationImpl sessionAuthorize;
	private SessionConfig config;
	private String username;
	private String password;
	private String seed;

	public PasswordTokenRequest(SessionAuthenticationImpl sessionAuthorize, SessionConfig config, String username,
			String password, String seed) {
		super();
		this.sessionAuthorize = sessionAuthorize;
		this.config = config;
		this.username = username;
		this.password = password;
		this.seed = seed;
	}

	@Override
	public void execute() { // String seed
		String authLink = config.getPasswordTokenGetUrl(username, password, seed);
		if (authLink == null) {
			log.error("Failed creating url for: " + username + "/" + password + "/" + seed);
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE1);
			return;
		}

		URLStreamBuilder builder = new URLStreamBuilderImpl().url(authLink).timeout(config.getConnectionTimeout());
		URLStream stream = builder.build();
		URLStreamStatus status = builder.getStatus();
		ByteArrayOutputStream out = stream.getOutputStream();

		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + authLink);
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE1);
			return;
		}

		String response = out.toString();
		log.info("response: " + response);
		StringTokenizer toks = new StringTokenizer(response, "\r\n");
		if (toks.countTokens() <= 0) {
			log.warn("Login Failed, wrong response in stage 1");
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE1);
			return;
		}

		int responseNo = -1;
		try {
			responseNo = Integer.valueOf(toks.nextToken());
		}
		catch (NumberFormatException e) {
			log.warn("Login Failed, wrong response in stage 1");
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE1);
			return;
		}

		if (responseNo != 0 || !toks.hasMoreTokens()) {
			switch (responseNo) {
			case 1235:
				log.info("Login Failed, Invalid username" + AuthenticationFailure.BADUSERNAME);
				sessionAuthorize.setFailureState(AuthenticationFailure.BADUSERNAME);
				break;
			case 1212:
				log.info("Login Failed, Wrong password: " + AuthenticationFailure.BAD);
				sessionAuthorize.setFailureState(AuthenticationFailure.BAD);
				break;
			case 1213:
				log.info("Login locked: Too many failed login attempts" + AuthenticationFailure.LOCKED);
				sessionAuthorize.setFailureState(AuthenticationFailure.LOCKED);
				break;
			case 1236:
				log.info("Login locked" + AuthenticationFailure.LOCKED);
				sessionAuthorize.setFailureState(AuthenticationFailure.LOCKED);
				break;
			case 100:
				log.info("Username or password missing" + AuthenticationFailure.BAD);
				sessionAuthorize.setFailureState(AuthenticationFailure.BAD);
				break;
			default:
				log.warn("Login Failed, unchecked error: " + responseNo);
				sessionAuthorize.setFailureState(AuthenticationFailure.NO_REASON);
			}
			return;
		}

		String ymsgr = toks.nextToken();

		if (ymsgr.indexOf("ymsgr=") == -1 && toks.hasMoreTokens()) {
			ymsgr = toks.nextToken();
		}

		ymsgr = ymsgr.replaceAll("ymsgr=", "");
		sessionAuthorize.setYmsgr(ymsgr);
		return;
	}

	@Override
	public void failure(Exception ex) {
		log.error("Failed token request", ex);
		sessionAuthorize.setFailureState(AuthenticationFailure.STAGE1);
	}
}
