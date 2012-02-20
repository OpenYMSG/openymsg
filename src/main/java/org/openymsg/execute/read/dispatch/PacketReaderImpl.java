package org.openymsg.execute.read.dispatch;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openymsg.execute.MultiplePacketResponse;
import org.openymsg.execute.PacketReader;
import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.execute.read.impl.ReaderRegistry;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class PacketReaderImpl implements PacketReader {
	private ReaderRegistry registry;
	private ConnectionReader reader;
	private ScheduledThreadPoolExecutor executor;

	public PacketReaderImpl(ScheduledThreadPoolExecutor executor) {
		this.executor = executor;
		this.registry = new ReaderRegistry();
	}

	public void setConnection(ConnectionHandler connection) {
		this.reader = new ConnectionReader(connection, this.registry);
		this.executor.scheduleWithFixedDelay(this.reader, 0, 500, TimeUnit.MILLISECONDS);
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
		this.executor.remove(this.reader);
		this.registry.clear();
	}

}
