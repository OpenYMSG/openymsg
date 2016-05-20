package org.openymsg.connection;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.openymsg.config.SessionConfig;
import org.openymsg.connection.read.PacketReaderServiceImpl;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.connection.write.PacketWriterServiceImpl;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.MessageRequest;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.ServiceType;

public class SessionConnectionBuilder {
	private SessionConnectionCallback callback;
	protected Executor executor;
	private SessionConfig sessionConfig;
	private ConnectionStateAndDetails connectionState;

	public SessionConnectionBuilder setCallback(SessionConnectionCallback callback) {
		this.callback = callback;
		return this;
	}

	public SessionConnectionBuilder setExecutor(Executor executor) {
		this.executor = executor;
		return this;
	}

	public SessionConnectionBuilder setSessionConfig(SessionConfig sessionConfig) {
		this.sessionConfig = sessionConfig;
		return this;
	}

	public SessionConnectionImpl build() {
		if (sessionConfig == null) {
			throw new IllegalStateException("sessionConfig must not be null");
		}
		if (executor == null) {
			throw new IllegalStateException("executor must not be null");
		}
		if (callback == null) {
			throw new IllegalStateException("callback must not be null");
		}
		ConcurrentLinkedQueue<MessageRequest> queue = new ConcurrentLinkedQueue<MessageRequest>();
		PacketWriterServiceImpl writerService = createWriterService(queue);
		PacketReaderServiceImpl readerService = createReaderService();
		connectionState = new ConnectionStateAndDetails(executor, writerService, readerService, callback);
		SessionConnectionImpl sessionConnection = new SessionConnectionImpl(connectionState, writerService,
				readerService);
		executor.execute(createConnectionInitialize(connectionState));
		return sessionConnection;
	}

	protected PacketReaderServiceImpl createReaderService() {
		return new PacketReaderServiceImpl(executor, new HashMap<ServiceType, Set<SinglePacketResponse>>());
	}

	protected PacketWriterServiceImpl createWriterService(ConcurrentLinkedQueue<MessageRequest> queue) {
		return new PacketWriterServiceImpl(executor, queue);
	}

	protected ConnectionStateAndDetails getConnectionStateForTest() {
		return this.connectionState;
	}

	protected Request createConnectionInitialize(ConnectionStateAndDetails connectionState) {
		return new ConnectionInitalize(sessionConfig, connectionState);
	}

}
