package org.openymsg.execute.write;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Message;
import org.openymsg.execute.PacketWriter;
import org.openymsg.execute.Request;
import org.openymsg.network.ConnectionHandler;

public class PacketWriterImpl implements PacketWriter {
	private static final Log log = LogFactory.getLog(PacketWriterImpl.class);
	private ScheduledThreadPoolExecutor executor = null;
	private ConnectionHandler connection = null;
	private boolean shutdown = false;

	public PacketWriterImpl(ScheduledThreadPoolExecutor executor) {
		this.executor = executor;
	}
	
	public void setConnection(ConnectionHandler connection) {
		this.connection = connection;
	}

	@Override
	public void execute(Message message) {
		if (connection == null) {
			log.info("Requeing messasge because connection is not set: " + message);
			Request request = new WaitForConnectionRequest(message, this);
			this.schedule(request, 100);
		}
		else {
			DispatcherMessageRequest request = new DispatcherMessageRequest(message, connection);
			this.executor.execute(request);
		}
	}

	public void execute(Message message, int delayInMillis) {
		if (connection == null) {
			throw new IllegalStateException("Connection is not set yet");
		}
		DispatcherMessageRequest request = new DispatcherMessageRequest(message, connection);
		schedule(request, delayInMillis);
	}

	private void schedule(Request request, int delayInMillis) {
		if (this.shutdown) {
			log.warn("Not executing: " + request + ", " + this.executor.isShutdown());
		}
		else {
			this.executor.schedule(request, delayInMillis, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void shutdown() {
		this.shutdown = true;
	}
}
