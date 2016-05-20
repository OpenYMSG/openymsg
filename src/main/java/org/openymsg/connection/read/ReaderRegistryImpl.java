package org.openymsg.connection.read;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ServiceType;

public class ReaderRegistryImpl implements ReaderRegistry {
	/** logger */
	private static final Log log = LogFactory.getLog(ReaderRegistryImpl.class);
	private final Map<ServiceType, Set<SinglePacketResponse>> registry;

	public ReaderRegistryImpl(Map<ServiceType, Set<SinglePacketResponse>> registry) {
		this.registry = registry;
	}

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
