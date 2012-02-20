package org.openymsg.auth;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.AuthenticationState;
import org.openymsg.SessionConfig;
import org.openymsg.execute.Request;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamBuilderImpl;
import org.openymsg.network.url.URLStreamBuilderStatus;

/**
 * Open a HTTP connection with a get token URL with the user's credentials and retrieve authorization and a token.
 * 
 * @author neilhart
 * 
 */
public class PasswordTokenRequest implements Request {
	private static final Log log = LogFactory.getLog(PasswordTokenRequest.class);
	private final SessionAuthorizeImpl sessionAuthorize;
	private SessionConfig config;
	private String username;
	private String password;
	private String seed;

	public PasswordTokenRequest(SessionAuthorizeImpl sessionAuthorize, SessionConfig config, String username,
			String password, String seed) {
		super();
		this.sessionAuthorize = sessionAuthorize;
		this.config = config;
		this.username = username;
		this.password = password;
		this.seed = seed;
	}

	@Override
	public void run() {
		this.yahooAuth16Stage1();
	}

	private void yahooAuth16Stage1() { // String seed
		String authLink = config.getPasswordTokenGetUrl(username, password, seed);
		if (authLink == null) {
			log.error("Failed creating url for: " + username + "/" + password + "/" + seed);
			// TODO handle failure
			return;
		}

		URLStreamBuilder builder = new URLStreamBuilderImpl().url(authLink).timeout(config.getConnectionTimeout())
				.keepData(true);
		URLStreamBuilderStatus status = builder.build();
		ByteArrayOutputStream out = builder.getOutputStream();

		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + authLink);
			// TODO handle failure
			return;
		}

		String response = out.toString();
		StringTokenizer toks = new StringTokenizer(response, "\r\n");
		if (toks.countTokens() <= 0) {
			log.warn("Login Failed, wrong response in stage 1");
			// TODO handle failure
			return;
		}

		int responseNo = -1;
		try {
			responseNo = Integer.valueOf(toks.nextToken());
		}
		catch (NumberFormatException e) {
			log.warn("Login Failed, wrong response in stage 1");
			// TODO handle failure
			return;
		}

		if (responseNo != 0 || !toks.hasMoreTokens()) {
			switch (responseNo) {
			case 1235:
				log.info("Login Failed, Invalid username" + AuthenticationState.BADUSERNAME);
				sessionAuthorize.setState(AuthenticationState.BADUSERNAME);
				break;
			case 1212:
				log.info("Login Failed, Wrong password" + AuthenticationState.BAD);
				sessionAuthorize.setState(AuthenticationState.BAD);
				break;
			case 1213:
				log.info("Login locked: Too many failed login attempts" + AuthenticationState.LOCKED);
				sessionAuthorize.setState(AuthenticationState.LOCKED);
				break;
			case 1236:
				log.info("Login locked" + AuthenticationState.LOCKED);
				sessionAuthorize.setState(AuthenticationState.LOCKED);
				break;
			case 100:
				log.info("Username or password missing" + AuthenticationState.BAD);
				sessionAuthorize.setState(AuthenticationState.BAD);
				break;
			default:
				log.info("Login Failed, Unkown error" + AuthenticationState.BAD);
				sessionAuthorize.setState(AuthenticationState.BAD);
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
}
