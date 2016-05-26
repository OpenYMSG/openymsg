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
	private final SessionAuthenticationAttemptCallback attemptCallback;
	private SessionConfig config;
	private AuthenticationToken token;

	public PasswordTokenLoginRequest(SessionAuthenticationAttemptCallback attemptCallback, SessionConfig config,
			AuthenticationToken token) {
		this.attemptCallback = attemptCallback;
		this.config = config;
		this.token = token;
	}

	@Override
	public void execute() {
		String ymsgr = token.getYmsgr();
		String loginLink = config.getPasswordTokenLoginUrl(ymsgr);
		URLStreamBuilder builder = config.getURLStreamBuilder().url(loginLink).timeout(config.getConnectionTimeout())
				.disableSSLCheck(config.isSSLCheckDisabled());
		URLStream stream = builder.build();
		URLStreamStatus status = builder.getStatus();
		ByteArrayOutputStream out = stream.getOutputStream();
		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + loginLink);
			URLStreamAuthenticationFailureHandler handler = new URLStreamAuthenticationFailureHandler(attemptCallback,
					AuthenticationStep.PasswordTokenLoginRequest);
			status.call(handler);
			return;
		}
		int responseNo = -1;
		String crumb = null;
		String cookieY = null;
		String cookieT = null;
		String response = out.toString();
		log.info("Received PasswordTokenLoginResponse: " + response);
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
			String error = "Login Failed, no token in response: " + response;
			attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenLoginRequest,
					new AuthenticationAttemptStatusImpl(error));
			log.warn(error);
			return;
		}
		try {
			responseNo = Integer.valueOf(toks.nextToken());
		} catch (NumberFormatException e) {
			String error = "Login Failed, invalid token in response: " + response;
			attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenLoginRequest,
					new AuthenticationAttemptStatusImpl(error));
			log.warn(error);
			return;
		}
		if (responseNo != 0 || !toks.hasMoreTokens()) {
			if (responseNo == 100) {
				String error = "Login Failed, Two Factor Authentication";
				attemptCallback.setFailureState(AuthenticationFailure.TWO_FACTOR_AUTHENTICATION);
				log.warn(error);
			} else {
				String error = "Login Failed, response code=" + responseNo;
				attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenLoginRequest,
						new AuthenticationAttemptStatusImpl(error));
				log.warn(error);
			}
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
			String error = "Login Failed, Cookies not found in response: " + response;
			attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenLoginRequest,
					new AuthenticationAttemptStatusImpl(error));
			log.warn(error);
			return;
		}
		token.setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
		this.attemptCallback.receivedPasswordTokenLogin();
	}

	@Override
	public void failure(Exception ex) {
		log.error("Failed token login", ex);
		attemptCallback.setConnectionFailureStatus(AuthenticationStep.PasswordTokenLoginRequest,
				new AuthenticationAttemptStatusImpl("Failed token login" + ex));
	}

	@Override
	public String toString() {
		return String.format("PasswordTokenLoginRequest [token=%s]", token);
	}
}
