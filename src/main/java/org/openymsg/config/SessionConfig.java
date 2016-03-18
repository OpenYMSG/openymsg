package org.openymsg.config;

import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.url.URLStreamBuilder;

import java.io.UnsupportedEncodingException;

/**
 * Configuration for the communicating to Yahoo. Properties should be static once the Session has the instance. This
 * should have instruction for either the direct connection or http.
 * @author neilhart
 */
public interface SessionConfig {
	int SECOND = 1000;

	String getLoginHost();

	ConnectionBuilder getConnectionBuilder();

	URLStreamBuilder getURLStreamBuilder();

	/**
	 * Login URL
	 * @param username user's name
	 * @param password user's password
	 * @param seed - crypto token from yahoo
	 * @return login url
	 * @throws UnsupportedEncodingException
	 */
	String getPasswordTokenGetUrl(String username, String password, String seed);

	String getPasswordTokenLoginUrl(String token);

	String[] getCapacityHosts();

	/**
	 * Socket size. Null or 0 values prevent a change to the system settings. This does not always work on different
	 * platforms. Only used by DirectConnect
	 * @return socket size
	 */
	Integer getSocketSize();

	/**
	 * Timeout for the socket connection in milliseconds. 0 is wait until the system default time Only used by
	 * DirectConnect
	 * @return timeout for attempted socket exception
	 */
	int getConnectionTimeout();

	String[] getScsHosts();

	/**
	 * Get timeout in seconds for session without calling keepalive. Null is never timeout.
	 * @return timeout in seconds, null is forever
	 */
	Integer getSessionTimeout();

	boolean isSSLCheckDisabled();
}
