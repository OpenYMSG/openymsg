package org.openymsg.context.auth;

/**
 * Authentication for Yahoo Messenger. Used to validate credentials
 * @author neilhart
 */
public interface SessionAuthentication {

	/**
	 * Log into Yahoo Messenger. This returns immediately. The response to the Login request will call the
	 * SessionAuthenticationCallback with either authenticationSuccess() or
	 * authenticationFailure(AuthenticationFailure).
	 * @param username user name
	 * @param password password
	 * @throws IllegalArgumentException if username or password are not valid
	 */
	// TODO - check username and password
	void login(String username, String password) throws IllegalArgumentException;

	AuthenticationFailure getFailureState();

}
