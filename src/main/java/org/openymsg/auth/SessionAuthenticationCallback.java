package org.openymsg.auth;

/**
 * Notifications of change in authentication
 * @author neilhart
 *
 */
public interface SessionAuthenticationCallback {
	/**
	 * Authentication was successful
	 */
	void authenticationSuccess();
	/**
	 * Authentication failed
	 * @param failure reason for failure
	 */
	void authenticationFailure(AuthenticationFailure failure);
}
