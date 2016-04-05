package org.openymsg.connection.write;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.network.ConnectionHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketWriterImpl implements PacketWriter {
	private Dispatcher executor = null;
	// TODO don't let this get to big
	private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
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
		this.queue.add(message);
	}

	@Override
	public void shutdown() {
		this.writer.finished();
		this.queue.clear();
	}
}
