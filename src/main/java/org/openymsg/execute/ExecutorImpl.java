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
	private Dispatcher dispatcher;

	public ExecutorImpl(String username) {
		DispatcherExecutorService executor = new DispatcherExecutorService(username);
		this.dispatcher = new DispatcherImpl(executor);
		this.writer = new PacketWriterImpl(this.dispatcher);
		this.reader = new PacketReaderImpl(this.dispatcher);
	}

	@Override
	public void register(ServiceType type, SinglePacketResponse response) {
		this.reader.register(type, response);
	}

	@Override
	public boolean deregister(ServiceType type, SinglePacketResponse response) {
		return this.reader.deregister(type, response);
	}

	@Override
	public void register(ServiceType type, MultiplePacketResponse response) {
		this.reader.register(type, response);
	}

	@Override
	public boolean deregister(ServiceType type, MultiplePacketResponse response) {
		return this.reader.deregister(type, response);
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
	public void execute(Request request) throws IllegalStateException {
		this.dispatcher.execute(request);
	}

	@Override
	public void shutdown() {
		if (connectionSet) {
			this.reader.shutdown();
			this.writer.shutdown();
		}
		this.dispatcher.shutdown();
	}

	@Override
	public void schedule(Request request, long repeatInterval) {
		this.dispatcher.schedule(request, repeatInterval);
	}
}
