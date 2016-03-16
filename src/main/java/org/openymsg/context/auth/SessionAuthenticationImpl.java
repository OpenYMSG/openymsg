package org.openymsg.context.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionAuthenticationImpl implements SessionAuthentication {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionAuthenticationImpl.class);
	private Executor executor;
	private YahooConnection connection;
	// private String username;
	// private String password;
	// private String seed;
	AuthenticationToken token;
	private SessionAuthenticationCallback callback;
	private SessionConfig sessionConfig;
	private AuthenticationFailure failureState;

	public SessionAuthenticationImpl(SessionConfig sessionConfig, YahooConnection connection, Executor executor,
			SessionAuthenticationCallback callback) {
		this.sessionConfig = sessionConfig;
		this.executor = executor;
		this.callback = callback;
		this.connection = connection;
		this.token = new AuthenticationToken();
		this.connection.register(ServiceType.AUTH, new LoginInitResponse(this, token));
		this.connection.register(ServiceType.AUTHRESP, new LoginFailureResponse(this));
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
		// this.username = username;
		// this.password = password;
		token.setUsernameAndPassword(username, password);
		// TODO move status check to Session
		// ConnectionState executionState = this.executor.getState();
		// if (executionState.isLoginable()) {
		connection.execute(new LoginInitMessage(username));
		// }
		// else {
		// throw new IllegalStateException("Don't call login when status is: " + executionState);
		// }
	}

	protected void setFailureState(AuthenticationFailure failureState) {
		log.info("Failed login: " + failureState);
		this.failureState = failureState;
		this.callback.authenticationFailure(failureState);
	}

	protected void receivedLoginInit() {
		this.executor.execute(new PasswordTokenRequest(this, sessionConfig, token));
	}

	protected void receivedPasswordToken() {
		this.executor.execute(new PasswordTokenLoginRequest(this, sessionConfig, token));
	}

	protected void receivedPasswordTokenLogin() {
		this.connection.execute(new LoginCompleteMessage(token));
		this.callback.authenticationSuccess();
	}

	/**
	 * @return the failureState
	 */
	public AuthenticationFailure getFailureState() {
		return failureState;
	}

}
