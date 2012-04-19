package org.openymsg.auth;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.auth.challenge.ChalllengeRespondBuilder;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionAuthenticationImpl implements SessionAuthentication {
	private static final Log log = LogFactory.getLog(SessionAuthenticationImpl.class);
	private Executor executor;
	private String username;
	private String password;
	private String seed;
	// private Set<SessionAuthenticationCallback> listeners = Collections
	// .synchronizedSet(new HashSet<SessionAuthenticationCallback>());
	private SessionAuthenticationCallback callback;
	private SessionConfig sessionConfig;
	private AuthenticationFailure failureState;

	public SessionAuthenticationImpl(SessionConfig sessionConfig, Executor executor,
			SessionAuthenticationCallback callback) {
		this.sessionConfig = sessionConfig;
		this.executor = executor;
		this.callback = callback;
		this.executor.register(ServiceType.AUTH, new LoginInitResponse(this));
		this.executor.register(ServiceType.AUTHRESP, new LoginFailureResponse(this));
	}

	@Override
	public void login(String username, String password) throws IllegalArgumentException {
		log.trace("login: " + username + "/" + password);
		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException("username may not be null");
		}
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("password may not be null");
		}
		this.username = username;
		this.password = password;

		// TODO move status check to Session
		// ConnectionState executionState = this.executor.getState();
		// if (executionState.isLoginable()) {
		executor.execute(new LoginInitMessage(username));
		// }
		// else {
		// throw new IllegalStateException("Don't call login when status is: " + executionState);
		// }
	}

	// @Override
	// public void addListener(SessionAuthenticationCallback listener) {
	// this.listeners.add(listener);
	// }
	//
	// @Override
	// public boolean removeListener(SessionAuthenticationCallback listener) {
	// return this.listeners.remove(listener);
	// }

	protected void setFailureState(AuthenticationFailure failureState) {
		log.info("Failed login: " + failureState);
		this.failureState = failureState;
		this.callback.authenticationFailure(failureState);
		// this.executor.shutdown();
	}

	protected void setSeed(String seed) {
		this.seed = seed;
		this.executor.execute(new PasswordTokenRequest(this, sessionConfig, username, password, seed));
	}

	protected void setYmsgr(String ymsgr) {
		this.executor.execute(new PasswordTokenLoginRequest(this, sessionConfig, ymsgr));
	}

	protected void setCookiesAndCrumb(String cookieY, String cookieT, String crumb, String cookieB) {
		String challenge = null;
		try {
			challenge = new ChalllengeRespondBuilder().useV16().challenge(crumb + seed).build();
		}
		catch (NoSuchAlgorithmException e) {
			// TODO handle
			e.printStackTrace();
			return;
		}
		this.executor.execute(new LoginCompleteMessage(username, cookieY, cookieT, challenge, cookieB));
		this.callback.authenticationSuccess();
	}

}
