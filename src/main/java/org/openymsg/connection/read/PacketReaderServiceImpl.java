package org.openymsg.connection.read;

import java.util.Map;
import java.util.Set;

import org.openymsg.connection.ConnectionService;
import org.openymsg.execute.Executor;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class PacketReaderServiceImpl implements PacketReaderService, ConnectionService {
	protected final ConnectionService connectionReaderService;
	protected final Map<ServiceType, Set<SinglePacketResponse>> registry;
	protected final ReaderRegistry readerRegistry;

	public PacketReaderServiceImpl(Executor executor, Map<ServiceType, Set<SinglePacketResponse>> registry) {
		this(executor, registry, new ReaderRegistryImpl(registry));
	}

	protected PacketReaderServiceImpl(Executor executor, Map<ServiceType, Set<SinglePacketResponse>> registry,
			ReaderRegistry readerRegistry) {
		this(new ConnectionReaderServiceImpl(executor, registry), registry, readerRegistry);
	}

	protected PacketReaderServiceImpl(ConnectionService connectionReaderService,
			Map<ServiceType, Set<SinglePacketResponse>> registry, ReaderRegistry readerRegistry) {
		this.connectionReaderService = connectionReaderService;
		this.registry = registry;
		this.readerRegistry = readerRegistry;
	}

	@Override
	public ReaderRegistry getReaderRegistry() {
		return this.readerRegistry;
	}

	@Override
	public void start(ConnectionHandler connection) {
		this.connectionReaderService.start(connection);
	}

	@Override
	public void stop() {
		this.connectionReaderService.stop();
		// TODO not sure about clearing, but it was there
		// this.registry.clear();
	}
}
