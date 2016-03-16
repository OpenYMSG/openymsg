package org.openymsg.network;

import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;

/**
 * Builder of ConnectionHandler. Connections are built by passing in a SessionConfig; picking capacity servers and/or
 * scs servers; and finally asking for the build to happen. After the build has succeeded or failed, information is
 * available for what attempts failed and what attempts were successful, if any. If both capacity servers and scs
 * servers are picked, capacity servers are used first.
 * @author neilhart
 */
public interface ConnectionBuilder {

	/**
	 * Get the information of what was tried in connecting to Yahoo and what failed.
	 * @return information about the connection
	 */
	ConnectionInfo getConnectionInfo();

	/**
	 * Setting the configuration of the connection. This must be set
	 * @param config connection configuration
	 * @return the instance as this is a builder patter
	 */
	ConnectionBuilder with(SessionConfig config);

	/**
	 * Set the builder to use the Capacity Servers
	 * @return the instance as this is a builder patter
	 */
	ConnectionBuilder useCapacityServers();

	/**
	 * Set the builder to use the scs servers
	 * @return the instance as this is a builder patter
	 */
	ConnectionBuilder useScsServers();

	/**
	 * Build the ConnectionHandler. This will return null if the connection cannot be built
	 * @return the connection or null if not successful
	 */
	ConnectionHandler build();

}