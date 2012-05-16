package org.openymsg.connection.read;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.execute.dispatch.ScheduleTaskCompletionException;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.YMSG9Packet;

public class ConnectionReader implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ConnectionReader.class);
	private ConnectionHandler connection;
	private ReaderRegistryImpl registry;
	private boolean isFinished = false;

	public ConnectionReader(ConnectionHandler connection, ReaderRegistryImpl registry) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		if (registry == null) {
			throw new IllegalArgumentException("Registry cannot be null");
		}
		this.connection = connection;
		this.registry = registry;
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
			registry.received(packet);
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

}
