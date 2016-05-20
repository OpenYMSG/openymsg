package org.openymsg.context.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.read.ReaderRegistry;
import org.openymsg.connection.write.Message;
import org.openymsg.connection.write.PacketWriter;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.ServiceType;

public class SessionAuthenticationImpl implements SessionAuthentication {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionAuthenticationImpl.class);
	private Executor executor;
	private final PacketWriter writer;
	AuthenticationToken token;
	private SessionAuthenticationCallback callback;
	private SessionConfig sessionConfig;
	private AuthenticationFailure failureState;

	public SessionAuthenticationImpl(SessionConfig sessionConfig, YahooConnection connection, Executor executor,
			SessionAuthenticationCallback callback) {
		this.sessionConfig = sessionConfig;
		this.executor = executor;
		this.callback = callback;
		this.writer = connection.getPacketWriter();
		this.token = new AuthenticationToken();
		ReaderRegistry registry = connection.getReaderRegistry();
		registry.register(ServiceType.AUTH, new LoginInitResponse(this, token));
		registry.register(ServiceType.AUTHRESP, new LoginFailureResponse(this));
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
		token.setUsernameAndPassword(username, password);
		// TODO move status check to Session
		// ConnectionState executionState = this.executor.getState();
		// if (executionState.isLoginable()) {
		writer.execute(new LoginInitMessage(username));
		// }
		// else {
		// throw new IllegalStateException("Don't call login when status is: " +
		// executionState);
		// }
	}

	protected void setFailureState(AuthenticationFailure failureState) {
		log.info("Failed login: " + failureState);
		this.failureState = failureState;
		this.callback.authenticationFailure(failureState);
	}

	protected void receivedLoginInit() {
		execute(new PasswordTokenRequest(this, sessionConfig, token));
	}

	protected void receivedPasswordToken() {
		execute(new PasswordTokenLoginRequest(this, sessionConfig, token));
	}

	protected void execute(Request request) {
		this.executor.execute(request);
	}

	protected void execute(Message message) {
		this.writer.execute(message);
	}

	protected void receivedPasswordTokenLogin() {
		execute(new LoginCompleteMessage(token));
		this.callback.authenticationSuccess();
	}

	/**
	 * @return the failureState
	 */
	@Override
	public AuthenticationFailure getFailureState() {
		return failureState;
	}
}
