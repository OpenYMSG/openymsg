package org.openymsg.execute.read;

import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public interface PacketReader {
	void register(ServiceType type, SinglePacketResponse response);

	void deregister(ServiceType type, SinglePacketResponse response);

	void register(ServiceType type, MultiplePacketResponse response);

	void deregister(ServiceType type, MultiplePacketResponse response);

	void initializeConnection(ConnectionHandler connection);

	void shutdown();
}
