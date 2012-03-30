package org.openymsg.auth;

import java.io.ByteArrayOutputStream;
import java.util.List;
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
 * Open a HTTP connection with a login URL with a generated token and retrieve some cookies and a crumb.
 * @author neilhart
 */
public class PasswordTokenLoginRequest implements Request {
	private static final Log log = LogFactory.getLog(PasswordTokenLoginRequest.class);
	private final SessionAuthenticationImpl sessionAuthorize;
	private SessionConfig config;
	private String token;

	public PasswordTokenLoginRequest(SessionAuthenticationImpl sessionAuthorize, SessionConfig config, String token) {
		this.sessionAuthorize = sessionAuthorize;
		this.config = config;
		this.token = token;
	}

	@Override
	public void execute() {
		this.yahooAuth16Stage2(token);
	}

	private void yahooAuth16Stage2(String token) {
		String loginLink = config.getPasswordTokenLoginUrl(token);

		URLStreamBuilder builder = new URLStreamBuilderImpl().url(loginLink).timeout(config.getConnectionTimeout());
		URLStream stream = builder.build();
		URLStreamStatus status = builder.getStatus();
		ByteArrayOutputStream out = stream.getOutputStream();

		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + loginLink);
			// TODO handle failure
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
		}
		catch (NumberFormatException e) {
			log.warn("Login Failed, wrong response in stage 2:");
			sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
			return;
		}

		if (responseNo != 0 || !toks.hasMoreTokens()) {
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

		// Iterator<String> iter =
		// ((HttpURLConnection) uc).getHeaderFields().get("Set-Cookie").iterator();
		// while (iter.hasNext())
		// {
		// String string = iter.next();
		// System.out.println("\t" + string);
		// }
		this.sessionAuthorize.setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
		// return yahooAuth16Stage3(crumb + seed, cookieY, cookieT);

		// log.warn("Login Failed, unable to retrieve stage 2 url");
	}

	@Override
	public void failure(Exception ex) {
		log.error("Failed token login", ex);
		sessionAuthorize.setFailureState(AuthenticationFailure.STAGE2);
	}

}
