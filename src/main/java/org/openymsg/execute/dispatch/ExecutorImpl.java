package org.openymsg.execute.dispatch;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openymsg.execute.Executor;
import org.openymsg.execute.Message;
import org.openymsg.execute.MultiplePacketResponse;
import org.openymsg.execute.Request;
import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.execute.read.dispatch.PacketReaderImpl;
import org.openymsg.execute.write.PacketWriterImpl;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ServiceType;

public class ExecutorImpl implements Executor {
	private ScheduledThreadPoolExecutor executor = null;
	boolean connectionSet = false;
	private PacketWriterImpl writer;
	private PacketReaderImpl reader;
	private SimpleExecutor simple;
	
	public ExecutorImpl(String username) {
		this.executor = new DispatcherScheduledExecutorService(username);
		this.writer = new PacketWriterImpl(this.executor);
		this.reader = new PacketReaderImpl(this.executor);
		this.simple = new SimpleExecutor(this.executor);
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

	public void initializeConnection(ConnectionHandler connection) throws IllegalStateException{
		if (connectionSet) {
			throw new IllegalStateException("Connection was already set");
		}
		this.connectionSet = true;
		this.reader.setConnection(connection);
		this.writer.setConnection(connection);
	}

	@Override
	public void execute(Message message) {
		this.writer.execute(message);
	}

	@Override
	public void execute(Request request) {
		this.simple.execute(request);
//		if (this.shutdown) {
//			log.warn("Not executing: " + request + ", " + this.executor.isShutdown());
//		}
//		else {
//			executor.execute(request);
//		}
	}

	@Override
	public void shutdown() {
		this.reader.shutdown();
		this.writer.shutdown(); 
		this.simple.shutdown();
	}

	@Override
	public void schedule(Runnable command, int repeatTimeInMillis) {
		this.executor.scheduleAtFixedRate(command, 0, repeatTimeInMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void schedule(Message message, int repeatTimeInMillis) {
		throw new RuntimeException();
		// TODO Auto-generated method stub
		
	}

}
