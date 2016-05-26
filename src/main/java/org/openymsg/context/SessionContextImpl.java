package org.openymsg.context;

import org.openymsg.YahooStatus;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.YahooConnection;
import org.openymsg.context.auth.AuthenticationFailure;
import org.openymsg.context.auth.SessionAuthenticationImpl;
import org.openymsg.context.session.LogoutReason;
import org.openymsg.context.session.SessionSessionImpl;
import org.openymsg.execute.Executor;

public class SessionContextImpl implements SessionContext, SessionContextCallback {
	protected final SessionAuthenticationImpl authentication;
	protected final SessionSessionImpl session;
	private final SessionContextCallback callback;
	private boolean isInvisible;

	public SessionContextImpl(SessionConfig sessionConfig, Executor executor, YahooConnection connection,
			String username, SessionContextCallback callback) {
		this.callback = callback;
		authentication = createAuthentication(sessionConfig, executor, connection);
		session = createSessionSession(sessionConfig, executor, connection, username);
	}

	protected SessionSessionImpl createSessionSession(SessionConfig sessionConfig, Executor executor,
			YahooConnection connection, String username) {
		return new SessionSessionImpl(username, executor, connection, sessionConfig.getSessionTimeout(), this);
	}

	protected SessionAuthenticationImpl createAuthentication(SessionConfig sessionConfig, Executor executor,
			YahooConnection connection) {
		return new SessionAuthenticationImpl(sessionConfig, connection, executor, this);
	}

	@Override
	// TODO already have username
	public void login(String username, String password, boolean isInvisible) throws IllegalArgumentException {
		this.isInvisible = isInvisible;
		authentication.login(username, password, isInvisible);

	}

	@Override
	public void logout() {
		session.logout();
	}

	@Override
	public void setStatus(YahooStatus status) throws IllegalArgumentException {
		session.setStatus(status);
		isInvisible = (status == YahooStatus.INVISIBLE);
	}

	@Override
	public void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		session.setCustomStatus(message, showBusyIcon);
	}

	@Override
	public void authenticationSuccess() {
		session.loginComplete(isInvisible);
		callback.authenticationSuccess();
	}

	@Override
	public void authenticationFailure(AuthenticationFailure failure) {
		callback.authenticationFailure(failure);
	}

	@Override
	public void logoffNormalComplete() {
		callback.logoffNormalComplete();
	}

	@Override
	public void logoffForced(LogoutReason state) {
		callback.logoffForced(state);
	}

	@Override
	public AuthenticationFailure getFailureState() {
		return authentication.getFailureState();
	}

	public void receivedLogout(LogoutReason state) {
		session.receivedLogout(state);
	}

	@Override
	public void keepAlive() {
		session.keepAlive();
	}
}
