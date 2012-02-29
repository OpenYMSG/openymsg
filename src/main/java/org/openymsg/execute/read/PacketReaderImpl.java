package org.openymsg.execute.read;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class PacketReaderImpl implements PacketReader {
	private ReaderRegistry registry;
	private ConnectionReader reader;
	private Dispatcher executor;

	public PacketReaderImpl(Dispatcher executor) {
		this.executor = executor;
		this.registry = new ReaderRegistry();
	}

	@Override
	public void initializeConnection(ConnectionHandler connection) {
		this.reader = new ConnectionReader(connection, this.registry);
		this.executor.schedule(this.reader, 500);
	}

	@Override
	public void register(ServiceType type, SinglePacketResponse response) {
		this.registry.register(type, response);
	}

	@Override
	public void deregister(ServiceType type, SinglePacketResponse response) {
		this.registry.deregister(type, response);
	}

	@Override
	public void register(ServiceType type, MultiplePacketResponse response) {
		this.registry.register(type, response);
	}

	@Override
	public void deregister(ServiceType type, MultiplePacketResponse response) {
		this.registry.deregister(type, response);
	}

	@Override
	public void shutdown() {
		this.reader.finished();
		this.registry.clear();
	}

}
