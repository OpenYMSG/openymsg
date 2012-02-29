package org.openymsg.execute.read;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.YMSG9Packet;

public class ConnectionReader implements Request {
	private static final Log log = LogFactory.getLog(ConnectionReader.class);
	private ConnectionHandler connection;
	private ReaderRegistry registry;
	private boolean isFinished = false;

	public ConnectionReader(ConnectionHandler connection, ReaderRegistry registry) {
		this.connection = connection;
		this.registry = registry;
	}

	@Override
	public void execute() {
		log.trace("Running");
		if (this.isFinished) {
			log.info("Running when finished");
			return;
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
	public void failure(Exception ex) {
		// TODO Auto-generated method stub
		log.error("Failed reading connection", ex);
	}

}
