package org.openymsg.connection.read;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ServiceType;
import org.openymsg.network.YMSG9Packet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReaderRegistryImpl implements ReaderRegistry {
	/** logger */
	private static final Log log = LogFactory.getLog(ReaderRegistryImpl.class);
	private Map<ServiceType, Set<SinglePacketResponse>> registry =
			new HashMap<ServiceType, Set<SinglePacketResponse>>();

	public ReaderRegistryImpl() {}

	@Override
	public void register(ServiceType type, SinglePacketResponse response) {
		if (type == null) {
			throw new IllegalArgumentException("type may not be null");
		}
		if (response == null) {
			throw new IllegalArgumentException("response may not be null");
		}
		Set<SinglePacketResponse> responses = this.registry.get(type);
		if (responses == null) {
			responses = new HashSet<SinglePacketResponse>();
			this.registry.put(type, responses);
		}
		if (responses.contains(response)) {
			log.warn("Callback already registered for type: " + type + ", " + response.getClass().getSimpleName());
		} else {
			responses.add(response);
		}
	}

	@Override
	public boolean deregister(ServiceType type, SinglePacketResponse response) {
		if (type == null) {
			throw new IllegalArgumentException("type may not be null");
		}
		if (response == null) {
			throw new IllegalArgumentException("response may not be null");
		}
		Set<SinglePacketResponse> responses = this.registry.get(type);
		if (responses != null && !responses.contains(response)) {
			log.warn("Callback was not registered for: " + type + ", " + response.getClass().getSimpleName());
		} else {
			if (responses != null) {
				return responses.remove(response);
			}
		}
		return false;
	}

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
				response.execute(packet);
			} catch (Exception e) {
				log.error("Failed calling: " + packet, e);
			}
		}
	}

	@Override
	public void register(ServiceType type, MultiplePacketResponse response) {
		if (type == null) {
			throw new IllegalArgumentException("type may not be null");
		}
		if (response == null) {
			throw new IllegalArgumentException("response may not be null");
		}
		CollectPacketResponse collector = new CollectPacketResponse(response);
		this.register(type, collector);
	}

	@Override
	public boolean deregister(ServiceType type, MultiplePacketResponse response) {
		if (type == null) {
			throw new IllegalArgumentException("type may not be null");
		}
		if (response == null) {
			throw new IllegalArgumentException("response may not be null");
		}
		CollectPacketResponse collector = new CollectPacketResponse(response);
		return this.deregister(type, collector);
	}

	public void clear() {
		this.registry.clear();
	}
}
