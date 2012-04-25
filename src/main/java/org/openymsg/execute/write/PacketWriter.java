package org.openymsg.execute.write;

import org.openymsg.network.ConnectionHandler;

public interface PacketWriter {
	void execute(Message message);

	void initializeConnection(ConnectionHandler connection);

	void shutdown();
}
