package org.openymsg.connection.read;

import java.util.Map;
import java.util.Set;

import org.openymsg.connection.ConnectionService;
import org.openymsg.execute.Executor;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class ConnectionReaderServiceImpl implements ConnectionService {
	protected final Executor executor;
	protected final ConnectionReaderReceiver receiver;
	private ConnectionReaderJob reader;

	public ConnectionReaderServiceImpl(Executor executor, Map<ServiceType, Set<SinglePacketResponse>> registry) {
		this(executor, new PacketReaderImpl(registry));
	}

	protected ConnectionReaderServiceImpl(Executor executor, ConnectionReaderReceiver receiver) {
		if (executor == null) {
			throw new IllegalArgumentException("Executor cannot be null");
		}
		if (receiver == null) {
			throw new IllegalArgumentException("receiver cannot be null");
		}
		this.executor = executor;
		this.receiver = receiver;
	}

	@Override
	public void start(ConnectionHandler connection) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		this.reader = new ConnectionReaderJob(connection, receiver);
		this.executor.schedule(this.reader, 100);
	}

	@Override
	public void stop() {
		this.reader.finished();
	}
}
