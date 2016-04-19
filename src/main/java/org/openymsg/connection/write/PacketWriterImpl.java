package org.openymsg.connection.write;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.execute.dispatch.MessageRequest;
import org.openymsg.network.ConnectionHandler;

public class PacketWriterImpl implements PacketWriter {
	private Dispatcher executor = null;
	// TODO don't let this get to big
	private ConcurrentLinkedQueue<MessageRequest> queue = new ConcurrentLinkedQueue<MessageRequest>();
	private ConnectionWriter writer;

	// TODO only schedule when queue is active
	public PacketWriterImpl(Dispatcher executor) {
		this.executor = executor;
		this.writer = new ConnectionWriter(queue, this.executor);
		this.executor.schedule(this.writer, 200);
	}

	public void initializeConnection(ConnectionHandler connection) {
		this.writer.setConnection(connection);
	}

	@Override
	public void execute(Message message) {
		if (writer.isLocked(2000)) {
			// TODO what is this for?
		}
		MessageRequest request = wrapMessage(message);
		this.queue.add(request);
	}

	protected MessageRequest wrapMessage(Message message) {
		MessageRequest request = new MessageExecuteRequest(message);
		return request;
	}

	@Override
	public void shutdown() {
		this.writer.finished();
		this.queue.clear();
	}
}
