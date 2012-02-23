package org.openymsg.network;

import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;

public interface ConnectionBuilder {

	ConnectionInfo getHandlerStatus();

	ConnectionBuilder with(SessionConfig config);

	ConnectionBuilder useCapacityServers();

	ConnectionBuilder useScsServers();

	ConnectionHandler build();

}