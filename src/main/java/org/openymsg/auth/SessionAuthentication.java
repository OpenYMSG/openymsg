package org.openymsg.auth;

/**
 * Authentication for Yahoo Messenger. Used to validate credentials
 * 
 * @author neilhart
 */
public interface SessionAuthentication {

	/**
	 * Log into Yahoo Messenger. This returns immediately. The response to the Login request will call the
	 * SessionAuthenticationCallback with either authenticationSuccess() or
	 * authenticationFailure(AuthenticationFailure).
	 * 
	 * @param username user name
	 * @param password password
	 * @throws IllegalArgumentException if username or password are not valid
	 */
	// TODO - check username and password
	void login(String username, String password) throws IllegalArgumentException;

	/**
	 * Register interest in changes. The callback method will be called during authentication.
	 * 
	 * @param listener callback for notification of changes
	 */
	void addListener(SessionAuthenticationCallback listener);

	/**
	 * Remove interest in changes.
	 * 
	 * @param listener callback for notication of changes
	 * @return true if found and removed.
	 */
	boolean removeListener(SessionAuthenticationCallback listener);

}
