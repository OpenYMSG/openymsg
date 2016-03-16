package org.openymsg.connection.read;

import org.openymsg.network.ServiceType;

public interface ReaderRegistry {

	void register(ServiceType type, SinglePacketResponse response);

	boolean deregister(ServiceType type, SinglePacketResponse response);

	void register(ServiceType type, MultiplePacketResponse response);

	boolean deregister(ServiceType type, MultiplePacketResponse response);

}