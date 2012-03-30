package org.openymsg.execute.write;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.network.ConnectionHandler;

public class PacketWriterImpl implements PacketWriter {
	private Dispatcher executor = null;
	// TODO don't let this get to big
	private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
	private ConnectionWriter reader;

	// TODO only schedule when queue is active
	public PacketWriterImpl(Dispatcher executor) {
		this.executor = executor;
		this.reader = new ConnectionWriter(queue, this.executor);
		this.executor.schedule(this.reader, 200);
	}

	@Override
	public void initializeConnection(ConnectionHandler connection) {
		this.reader.setConnection(connection);
	}

	@Override
	public void execute(Message message) {
		this.queue.add(message);
	}

	@Override
	public void shutdown() {
	}

}
