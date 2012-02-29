package org.openymsg.execute.read;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ServiceType;
import org.openymsg.network.YMSG9Packet;

public class ReaderRegistry {
	private static final Log log = LogFactory.getLog(ReaderRegistry.class);
	private Map<ServiceType, Set<SinglePacketResponse>> registry = new HashMap<ServiceType, Set<SinglePacketResponse>>();

	public ReaderRegistry() {
	}

	public void register(ServiceType type, SinglePacketResponse response) {
		if (type == null || response == null) {
			throw new IllegalArgumentException("type: " + type + " and response: " + response + " may not be null");
		}
		Set<SinglePacketResponse> responses = this.registry.get(type);
		if (responses == null) {
			responses = new HashSet<SinglePacketResponse>();
			this.registry.put(type, responses);
		}
		if (responses.contains(response)) {
			log.warn("Callback already registered for type: " + type + ", " + response.getClass().getSimpleName());
		}
		else {
			responses.add(response);
		}
	}

	public void deregister(ServiceType type, SinglePacketResponse response) {
		if (type == null || response == null) {
			throw new IllegalArgumentException("type: " + type + " and callback: " + response + " may not be null");
		}
		Set<SinglePacketResponse> responses = this.registry.get(type);
		if (responses != null && !responses.contains(response)) {
			log.warn("Callback was not registered for: " + type + ", " + response.getClass().getSimpleName());
		}
		else {
			responses.remove(response);
		}
	}

	public void received(YMSG9Packet packet) {
		log.info("received packet: " + packet);
		ServiceType type = packet.service;
		Set<SinglePacketResponse> responses = this.registry.get(type);
		if (responses == null || responses.isEmpty()) {
			log.info("Not handling serviceType: + " + type);
			return;
		}
		if (responses.size() > 1) {
			log.info("multiple responses for serviceType: + " + type);
		}
		for (SinglePacketResponse response : responses) {
			response.execute(packet);
		}

	}

	public void register(ServiceType type, MultiplePacketResponse response) {
		CollectPacketResponse collector = new CollectPacketResponse(response);
		this.register(type, collector);
	}

	public void deregister(ServiceType type, MultiplePacketResponse response) {
		CollectPacketResponse collector = new CollectPacketResponse(response);
		this.deregister(type, collector);
	}

	public void clear() {
		this.registry.clear();
	}
}
