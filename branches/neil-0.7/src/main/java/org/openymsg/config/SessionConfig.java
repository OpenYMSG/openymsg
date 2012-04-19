package org.openymsg.config;

import java.io.UnsupportedEncodingException;

import org.openymsg.network.ConnectionBuilder;

/**
 * Configuration for the communicating to Yahoo. Properties should be static once the Session has the instance. This
 * should have instruction for either the direct connection or http.
 * @author neilhart
 */
public interface SessionConfig {
	int SECOND = 1000;

	String getLoginHost();

	ConnectionBuilder getBuilder();

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
	 * @return
	 */
	Integer getSocketSize();

	/**
	 * Timeout for the socket connection in milliseconds. 0 is wait until the system default time Only used by
	 * DirectConnect
	 * @return
	 */
	int getConnectionTimeout();

	String[] getScsHosts();
}
