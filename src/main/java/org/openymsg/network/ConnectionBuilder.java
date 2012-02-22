package org.openymsg.network;

import org.openymsg.SessionConfig;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.network.direct.DirectConnectionHandler;

public interface ConnectionBuilder {

	public abstract ConnectionInfo getHandlerStatus();

	public abstract ConnectionBuilder with(SessionConfig config);

	public abstract ConnectionBuilder useCapacityServers();

	public abstract ConnectionBuilder useScsServers();

	public abstract DirectConnectionHandler build();

}