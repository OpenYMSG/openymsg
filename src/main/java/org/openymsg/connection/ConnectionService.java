package org.openymsg.connection;

import org.openymsg.network.ConnectionHandler;

public interface ConnectionService {
	void start(ConnectionHandler connection);

	void stop();
}
