package org.openymsg.context.auth;

public interface SessionAuthenticationAttemptCallback {
	void receivedPasswordTokenLogin();

	void setFailureState(AuthenticationFailure authenticationFailure);

	void setConnectionFailureStatus(AuthenticationStep step, AuthenticationAttemptStatus status);

	void receivedPasswordToken();
}
