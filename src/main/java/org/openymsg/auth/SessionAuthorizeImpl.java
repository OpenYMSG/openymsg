package org.openymsg.auth;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.AuthenticationState;
import org.openymsg.SessionConfig;
import org.openymsg.auth.challenge.ChalllengeRespondBuilder;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorState;
import org.openymsg.network.ServiceType;

public class SessionAuthorizeImpl implements SessionAuthorize {
	private static final Log log = LogFactory.getLog(SessionAuthorizeImpl.class);
	private Executor executor;
	private String username;
	private String password;
	private String seed;
	private SessionConfig sessionConfig;
	private AuthenticationState state;

	public SessionAuthorizeImpl(SessionConfig sessionConfig, Executor executor) {
		this.sessionConfig = sessionConfig;
		this.executor = executor;
		this.executor.register(ServiceType.AUTH, new LoginInitResponse(this));
		this.executor.register(ServiceType.AUTHRESP, new LoginFailureResponse(this));
		this.executor.register(ServiceType.LIST, new ListResponse());
	}

	/**
	 * Needs to be delayed a little bit
	 */
	@Override
	public void login(String username, String password) {
		log.trace("login: " + username + "/" + password);
		this.username = username;
		this.password = password;
		ExecutorState executionState = this.executor.getState();
		if (executionState.isLoginable()) {
			executor.execute(new LoginInitMessage(username));
		}
		else {
			throw new RuntimeException("Don't call login when status is: " + executionState);
		}
	}

	/**
	 * Logs off the current session.
	 * 
	 */
	@Override
	public void logout() {
		log.trace("logout: " + username);
		ExecutorState executionState = this.executor.getState();
		if (executionState.isConnected()) {
			executor.execute(new LogoutMessage(username));
		}
		else {
			log.info("Trying to logout when not connected: " + username);
		}
		this.executor.execute(new ShutdownRequest(this.executor));
	}

	public void setState(AuthenticationState state) {
		log.info("Failed login: " + state);
		this.state = state;
		this.executor.shutdown();
	}

	public void setSeed(String seed) {
		this.seed = seed;
		this.executor.execute(new PasswordTokenRequest(this, sessionConfig, username, password, seed));
	}

	public void setYmsgr(String ymsgr) {
		this.executor.execute(new PasswordTokenLoginRequest(this, sessionConfig, ymsgr));
	}

//	@Override
//	public void initializeResult(ConnectionHandler connection, ConnectionHandlerStatus connectionStatus,
//			ConnectionState status) {
//		log.info("initializeResult: " + connection + "/" + status + "/" + connectionStatus);
//		this.status = status;
//		if (connectionStatus.isConnected()) {
//			this.reader.setConnection(connection);
//		}
//		else {
//			this.dispatcher.shutdown();
//		}
//	}

	public void setCookiesAndCrumb(String cookieY, String cookieT, String crumb, String cookieB) {
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
	}

}
