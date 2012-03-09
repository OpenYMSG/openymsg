package org.openymsg.network.url;

/**
 * Builder of HTTP Requests.
 * @author neilhart
 */
public interface URLStreamBuilder {

	/**
	 * Set the URL
	 * @param url string representing the URL
	 * @return same instance as this is a builder pattern
	 */
	URLStreamBuilder url(String url);

	/**
	 * Set the timeout for the url connection
	 * @param timeout time in milliseconds
	 * @return same instance as this is a builder pattern
	 */
	URLStreamBuilder timeout(int timeout);

	/**
	 * Set the cookie
	 * @param cookie full cookie string
	 * @return same instance as this is a builder pattern
	 */
	URLStreamBuilder cookie(String cookie);

	/**
	 * Build the URL
	 * @return
	 */
	URLStream build();

	/**
	 * Get the status of the build process
	 * @return status of the build
	 */
	URLStreamStatus getStatus();
}