package org.openymsg.execute.read.dispatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.impl.ReaderRegistry;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.YMSG9Packet;

public class ConnectionReader implements Runnable {
	private static final Log log = LogFactory.getLog(ConnectionReader.class);
	private ConnectionHandler connection;
	private ReaderRegistry registry;
	private boolean isFinished = false;

	public ConnectionReader(ConnectionHandler connection, ReaderRegistry registry) {
		this.connection = connection;
		this.registry = registry;
	}

	@Override
	public void run() {
		log.trace("Running");
		if (this.isFinished) {
			log.info("Running when finished");
			return;
		}
		try {
			YMSG9Packet packet = connection.receivePacket();
			while (packet != null) {
				registry.received(packet);
				packet = connection.receivePacket();
			}
		}
		catch (Exception e) {
			log.error("Failed reading connection", e);
		}
	}

	public void finished() {
		this.isFinished = true;
	}

}
