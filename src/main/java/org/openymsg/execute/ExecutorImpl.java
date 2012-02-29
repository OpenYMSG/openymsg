package org.openymsg.execute;

import org.openymsg.execute.dispatch.Dispatcher;
import org.openymsg.execute.dispatch.DispatcherExecutorService;
import org.openymsg.execute.dispatch.DispatcherImpl;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.execute.read.MultiplePacketResponse;
import org.openymsg.execute.read.PacketReaderImpl;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.execute.write.Message;
import org.openymsg.execute.write.PacketWriterImpl;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class ExecutorImpl implements Executor {
	// private ScheduledThreadPoolExecutor executor = null;
	boolean connectionSet = false;
	private PacketWriterImpl writer;
	private PacketReaderImpl reader;
	private Dispatcher simple;

	public ExecutorImpl(String username) {
		DispatcherExecutorService executor = new DispatcherExecutorService(username);
		this.simple = new DispatcherImpl(executor);
		this.writer = new PacketWriterImpl(this.simple);
		this.reader = new PacketReaderImpl(this.simple);
	}

	@Override
	public void register(ServiceType type, SinglePacketResponse response) {
		this.reader.register(type, response);
	}

	@Override
	public void deregister(ServiceType type, SinglePacketResponse response) {
		this.reader.deregister(type, response);
	}

	@Override
	public void register(ServiceType type, MultiplePacketResponse response) {
		this.reader.register(type, response);
	}

	@Override
	public void deregister(ServiceType type, MultiplePacketResponse response) {
		this.reader.deregister(type, response);
	}

	public void initializeConnection(ConnectionHandler connection) throws IllegalStateException {
		if (connectionSet) {
			throw new IllegalStateException("Connection was already set");
		}
		this.connectionSet = true;
		this.reader.initializeConnection(connection);
		this.writer.initializeConnection(connection);
	}

	@Override
	public void execute(Message message) {
		this.writer.execute(message);
	}

	@Override
	public void execute(Request request) {
		this.simple.execute(request);
		// if (this.shutdown) {
		// log.warn("Not executing: " + request + ", " + this.executor.isShutdown());
		// }
		// else {
		// executor.execute(request);
		// }
	}

	@Override
	public void shutdown() {
		this.reader.shutdown();
		this.writer.shutdown();
		this.simple.shutdown();
	}

	@Override
	public void schedule(Request request, long repeatInterval) {
		this.simple.schedule(request, repeatInterval);
	}
}
