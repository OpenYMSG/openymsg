package org.openymsg.connection.read;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class PacketReaderImpl implements PacketReader {
	protected final ReaderRegistryImpl registry;
	private ConnectionReader reader;
	private final Dispatcher executor;

	public PacketReaderImpl(Dispatcher executor) {
		if (executor == null) {
			throw new IllegalArgumentException("Executor cannot be null");
		}
		this.executor = executor;
		this.registry = createRegistry();
	}

	protected ReaderRegistryImpl createRegistry() {
		return new ReaderRegistryImpl();
	}

	protected ReaderReceiver getReceiver() {
		return this.registry;
	}

	@Override
	public void initializeConnection(ConnectionHandler connection) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		this.reader = new ConnectionReader(connection, getReceiver());
		this.executor.schedule(this.reader, 100);
	}

	@Override
	public void register(ServiceType type, SinglePacketResponse response) {
		this.registry.register(type, response);
	}

	@Override
	public boolean deregister(ServiceType type, SinglePacketResponse response) {
		return this.registry.deregister(type, response);
	}

	@Override
	public void register(ServiceType type, MultiplePacketResponse response) {
		this.registry.register(type, response);
	}

	@Override
	public boolean deregister(ServiceType type, MultiplePacketResponse response) {
		return this.registry.deregister(type, response);
	}

	@Override
	public void shutdown() {
		this.reader.finished();
		this.registry.clear();
	}
}
