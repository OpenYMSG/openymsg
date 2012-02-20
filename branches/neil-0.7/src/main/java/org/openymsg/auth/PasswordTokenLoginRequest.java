package org.openymsg.auth;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.SessionConfig;
import org.openymsg.AuthenticationState;
import org.openymsg.execute.Request;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamBuilderImpl;
import org.openymsg.network.url.URLStreamBuilderStatus;

/**
 * Open a HTTP connection with a login URL with a generated token and retrieve some cookies and a crumb. 
 * @author neilhart
 *
 */
public class PasswordTokenLoginRequest implements Request {
	private static final Log log = LogFactory.getLog(PasswordTokenLoginRequest.class);
	private final SessionAuthorizeImpl sessionAuthorize;
	private SessionConfig config;
	private String token;

	public PasswordTokenLoginRequest(SessionAuthorizeImpl sessionAuthorize, SessionConfig config, String token) {
		this.sessionAuthorize = sessionAuthorize;
		this.config = config;
		this.token = token;
	}

	@Override
	public void run() {
		try {
			this.yahooAuth16Stage2(token);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void yahooAuth16Stage2(String token) {
		String loginLink = config.getPasswordTokenLoginUrl(token);

		URLStreamBuilder builder = new URLStreamBuilderImpl().url(loginLink).timeout(config.getConnectionTimeout()).keepData(true).keepHeaders(true);
		URLStreamBuilderStatus status = builder.build();
		ByteArrayOutputStream out = builder.getOutputStream();

		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + loginLink);
			// TODO handle failure
			return;
		}

		int responseNo = -1;
		String crumb = null;
		String cookieY = null;
		String cookieT = null;

		String reponse = out.toString();

		// TODO handle cookieB
		List<String> cookies = builder.getHeaders().get("Set-Cookie");
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
		StringTokenizer toks = new StringTokenizer(reponse, "\r\n");
		if (toks.countTokens() <= 0) {
			log.warn("Login Failed, wrong response in stage 2:");
			// TODO handle failure
			return;
		}

		try {
			responseNo = Integer.valueOf(toks.nextToken());
		}
		catch (NumberFormatException e) {
			log.warn("Login Failed, wrong response in stage 2:");
			// TODO handle failure
			return;
		}

		if (responseNo != 0 || !toks.hasMoreTokens()) {
			sessionAuthorize.setState(AuthenticationState.BAD);
			log.warn("Login Failed, Unkown error");
			// TODO handle failure
			return;
		}

		while (toks.hasMoreTokens()) {
			String t = toks.nextToken();
			if (t.startsWith("crumb=")) {
				crumb = t.replaceAll("crumb=", "");
			}
			else if (t.startsWith("Y=")) {
				cookieY = t.replaceAll("Y=", "");
			}
			else if (t.startsWith("T=")) {
				cookieT = t.replaceAll("T=", "");
			}
		}

		if (crumb == null || cookieT == null || cookieY == null) {
			sessionAuthorize.setState(AuthenticationState.BAD);
			log.warn("Login Failed, Unkown error");
			// TODO handle failure
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

//		log.warn("Login Failed, unable to retrieve stage 2 url");
	}

}
