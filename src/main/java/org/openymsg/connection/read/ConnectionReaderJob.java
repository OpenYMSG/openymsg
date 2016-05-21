package org.openymsg.connection.read;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.execute.dispatch.ScheduleTaskCompletionException;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.YMSG9Packet;

public class ConnectionReaderJob implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ConnectionReaderJob.class);
	private ConnectionHandler connection;
	private ConnectionReaderReceiver receiver;
	private boolean isFinished = false;

	public ConnectionReaderJob(ConnectionHandler connection, ConnectionReaderReceiver receiver) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		if (receiver == null) {
			throw new IllegalArgumentException("Receiver cannot be null");
		}
		this.connection = connection;
		this.receiver = receiver;
	}

	@Override
	public void execute() {
		// TODO - how long runs, starvation
		log.trace("Running");
		if (this.isFinished) {
			throw new ScheduleTaskCompletionException();
		}
		YMSG9Packet packet = connection.receivePacket();
		while (packet != null) {
			receiver.received(packet);
			packet = connection.receivePacket();
		}
	}

	public void finished() {
		this.isFinished = true;
	}

	@Override
	// TODO need to do something with this
	public void failure(Exception ex) {
		log.error("Failed reading connection", ex);
	}

	@Override
	public String toString() {
		return String.format("ConnectionReaderJob [isFinished=%s]", isFinished);
	}
}
