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
	private SessionAuthenticationImpl authentication;
	private SessionSessionImpl session;
	private SessionContextCallback callback;

	public SessionContextImpl(SessionConfig sessionConfig, Executor executor, YahooConnection connection,
			String username, SessionContextCallback callback) {
		this.callback = callback;
		authentication = new SessionAuthenticationImpl(sessionConfig, connection, executor, this);
		session = new SessionSessionImpl(username, executor, connection, this);
	}

	@Override
	// TODO already have username
	public void login(String username, String password) throws IllegalArgumentException {
		authentication.login(username, password);
	}

	@Override
	public void logout() {
		session.logout();
	}

	@Override
	public void setStatus(YahooStatus status) throws IllegalArgumentException {
		session.setStatus(status);
	}

	@Override
	public void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException {
		session.setCustomStatus(message, showBusyIcon);
	}

	@Override
	public void authenticationSuccess() {
		this.session.loginComplete();
		this.callback.authenticationSuccess();
	}

	@Override
	public void authenticationFailure(AuthenticationFailure failure) {
		this.callback.authenticationFailure(failure);
	}

	@Override
	public void logoffNormalComplete() {
		this.callback.logoffNormalComplete();
	}

	@Override
	public void logoffForced(LogoutReason state) {
		this.callback.logoffForced(state);
	}

	@Override
	public AuthenticationFailure getFailureState() {
		return authentication.getFailureState();
	}

}
