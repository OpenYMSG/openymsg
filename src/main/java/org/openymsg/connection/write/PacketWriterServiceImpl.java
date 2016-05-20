package org.openymsg.connection.write;

import java.util.Queue;

import org.openymsg.connection.ConnectionService;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.MessageRequest;
import org.openymsg.network.ConnectionHandler;

public class PacketWriterServiceImpl implements PacketWriterService, ConnectionService {
	// TODO don't let this get to big
	// private final ConcurrentLinkedQueue<MessageRequest> queue = new
	// ConcurrentLinkedQueue<MessageRequest>();
	private final Queue<MessageRequest> queue;
	private final ConnectionService connectionWriterService;
	private final PacketWriterImpl packetWriter;

	public PacketWriterServiceImpl(Executor executor, Queue<MessageRequest> queue) {
		this(queue, new PacketWriterImpl(queue), new ConnectionWriterServiceImpl(executor, queue));
	}

	protected PacketWriterServiceImpl(Queue<MessageRequest> queue, PacketWriterImpl packetWriter,
			ConnectionService connectionWriterService) {
		this.queue = queue;
		this.packetWriter = packetWriter;
		this.connectionWriterService = connectionWriterService;
	}

	@Override
	public void start(ConnectionHandler connection) {
		connectionWriterService.start(connection);
	}

	@Override
	public void stop() {
		this.queue.clear();
		connectionWriterService.stop();
	}

	@Override
	public PacketWriter getPacketWriter() {
		return this.packetWriter;
	}
}
