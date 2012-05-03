package org.openymsg.execute.write;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.ConnectionHandler;

public class ConnectionWriter implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ConnectionWriter.class);
	private ConnectionHandler connection;
	private ConcurrentLinkedQueue<Message> queue;
	private Dispatcher executor;

	public ConnectionWriter(ConcurrentLinkedQueue<Message> queue, Dispatcher executor) {
		this.queue = queue;
		this.executor = executor;
	}

	@Override
	public void execute() {
		if (connection == null) {
			log.info("connection not set");
			return;
		}
		Message message = this.queue.poll();
		if (message != null) {
			MessageExecuteRequest request = new MessageExecuteRequest(message, connection);
			this.executor.execute(request);
		} else {
			log.trace("message is null");
		}
	}

	public void setConnection(ConnectionHandler connection) {
		this.connection = connection;
	}

	@Override
	public void failure(Exception ex) {
		// TODO Handle failure,
		ex.printStackTrace();
	}

}
