package org.openymsg.execute;

import org.openymsg.network.ServiceType;

public interface PacketReader {
	void register(ServiceType type, SinglePacketResponse response);
	void deregister(ServiceType type, SinglePacketResponse response);
	void register(ServiceType type, MultiplePacketResponse response);
	void deregister(ServiceType type, MultiplePacketResponse response);
	void shutdown();
}
