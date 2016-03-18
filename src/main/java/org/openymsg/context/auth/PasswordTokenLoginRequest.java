package org.openymsg.context.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.url.URLStream;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamStatus;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Open a HTTP connection with a login URL with a generated token and retrieve some cookies and a crumb.
 * @author neilhart
 */
public class PasswordTokenLoginRequest implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(PasswordTokenLoginRequest.class);
	private final SessionAuthenticationImpl sessionAuthorize;
	private SessionConfig config;
	private AuthenticationToken token;

	public PasswordTokenLoginRequest(SessionAuthenticationImpl sessionAuthorize, SessionConfig config,
			AuthenticationToken token) {
		this.sessionAuthorize = sessionAuthorize;
		this.config = config;
		this.token = token;
	}

	@Override
	public void execute() {
		this.yahooAuth16Stage2(token.getYmsgr());
	}

	private void yahooAuth16Stage2(String ymsgr) {
		String loginLink = config.getPasswordTokenLoginUrl(ymsgr);
		URLStreamBuilder builder = config.getURLStreamBuilder().url(loginLink).timeout(config.getConnectionTimeout())
				.disableSSLCheck(config.isSSLCheckDisabled());
		URLStream stream = builder.build();
		URLStreamStatus status = builder.getStatus();
		ByteArrayOutputStream out = stream.getOutputStream();
		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + loginLink);
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
			return;
		}
		int responseNo = -1;
		String crumb = null;
		String cookieY = null;
		String cookieT = null;
		String response = out.toString();
		log.info("response: " + response);
		// TODO handle cookieB
		List<String> cookies = stream.getHeaders().get("Set-Cookie");
		log.info("cookies: " + cookies);
		String cookieB = null;
		for (String cookieLine : cookies) {
			StringTokenizer toks = new StringTokenizer(cookieLine, ";");
			while (toks.hasMoreTokens()) {
				String tok = toks.nextToken();
				if (tok.startsWith("B=")) {
					cookieB = tok.replaceAll("B=", "");
				}
			}
		}
		// StringTokenizer
		StringTokenizer toks = new StringTokenizer(response, "\r\n");
		if (toks.countTokens() <= 0) {
			log.warn("Login Failed, wrong response in stage 2:");
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
			return;
		}
		try {
			responseNo = Integer.valueOf(toks.nextToken());
		} catch (NumberFormatException e) {
			log.warn("Login Failed, wrong response in stage 2:");
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
			return;
		}
		if (responseNo != 0 || !toks.hasMoreTokens()) {
			if (responseNo == 100) {
				sessionAuthorize.setFailureState(AuthenticationFailure.TWO_FACTOR_AUTHENTICATION);
				log.warn("Login Failed, Two Factor Authentication");
				return;
			}
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
			log.warn("Login Failed, Unkown error");
			return;
		}
		while (toks.hasMoreTokens()) {
			String t = toks.nextToken();
			if (t.startsWith("crumb=")) {
				crumb = t.replaceAll("crumb=", "");
			} else if (t.startsWith("Y=")) {
				cookieY = t.replaceAll("Y=", "");
			} else if (t.startsWith("T=")) {
				cookieT = t.replaceAll("T=", "");
			}
		}
		if (crumb == null || cookieT == null || cookieY == null) {
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
			log.warn("Login Failed, Unkown error");
			return;
		}
		token.setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
		this.sessionAuthorize.receivedPasswordTokenLogin();
	}

	@Override
	public void failure(Exception ex) {
		log.error("Failed token login", ex);
		sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
	}
}
