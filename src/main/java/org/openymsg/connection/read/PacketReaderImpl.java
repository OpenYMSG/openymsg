package org.openymsg.connection.read;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ServiceType;
import org.openymsg.network.YMSG9Packet;

public class PacketReaderImpl implements ConnectionReaderReceiver {
	/** logger */
	private static final Log log = LogFactory.getLog(PacketReaderImpl.class);
	private final Map<ServiceType, Set<SinglePacketResponse>> registry;

	public PacketReaderImpl(Map<ServiceType, Set<SinglePacketResponse>> registry) {
		if (registry == null) {
			throw new IllegalArgumentException("registry cannot be null");
		}
		this.registry = registry;
	}

	@Override
	public void received(YMSG9Packet packet) {
		log.debug("received packet: " + packet);
		ServiceType type = packet.service;
		Set<SinglePacketResponse> responses = this.registry.get(type);
		if (responses == null || responses.isEmpty()) {
			log.warn("Not handling serviceType: + " + type);
			return;
		}
		if (responses.size() > 1) {
			log.warn("multiple responses for serviceType: + " + type);
		}
		for (SinglePacketResponse response : responses) {
			try {
				forwardPacket(packet, response);
			} catch (Exception e) {
				log.error("Failed calling: " + packet, e);
			}
		}
	}

	protected void forwardPacket(YMSG9Packet packet, SinglePacketResponse response) {
		response.execute(packet);
	}
}
