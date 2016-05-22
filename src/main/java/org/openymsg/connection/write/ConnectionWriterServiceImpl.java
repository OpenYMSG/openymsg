package org.openymsg.connection.write;

import java.util.Queue;

import org.openymsg.connection.ConnectionService;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.MessageRequest;
import org.openymsg.network.ConnectionHandler;

public class ConnectionWriterServiceImpl implements ConnectionService {
	protected final Executor executor;
	protected final Queue<MessageRequest> queue;
	protected ConnectionWriterJob writer;

	// TODO only schedule when queue is active
	public ConnectionWriterServiceImpl(Executor executor, Queue<MessageRequest> queue) {
		this.executor = executor;
		this.queue = queue;
	}

	public void start(ConnectionHandler connection) {
		this.writer = createWriterJob(connection);
		this.executor.schedule(this.writer, 200);
	}

	protected ConnectionWriterJob createWriterJob(ConnectionHandler connection) {
		return new ConnectionWriterJob(queue, connection);
	}

	@Override
	public void stop() {
		this.writer.finished();
	}
}
