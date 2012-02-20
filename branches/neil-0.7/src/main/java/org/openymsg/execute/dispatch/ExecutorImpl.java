package org.openymsg.execute.dispatch;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.SessionConfig;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorState;
import org.openymsg.execute.ExecutorStateListener;
import org.openymsg.execute.Message;
import org.openymsg.execute.MultiplePacketResponse;
import org.openymsg.execute.Request;
import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.execute.read.dispatch.PacketReaderImpl;
import org.openymsg.execute.write.PacketWriterImpl;
import org.openymsg.message.MessageMover;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ConnectionHandlerStatus;
import org.openymsg.network.ServiceType;

public class ExecutorImpl implements Executor {
	private static final Log log = LogFactory.getLog(ExecutorImpl.class);
	private ScheduledThreadPoolExecutor executor = null;
	private ExecutorStateMonitor monitor;
	PacketWriterImpl writer;
	PacketReaderImpl reader;
	SimpleExecutor simple;
	
	public ExecutorImpl(String username) {
		this.monitor = new ExecutorStateMonitor();
		this.executor = new DispatcherScheduledExecutorService(username);
		this.writer = new PacketWriterImpl(this.executor);
		this.reader = new PacketReaderImpl(this.executor);
		this.simple = new SimpleExecutor(this.executor);
	}
	
	public void initialize(SessionConfig sessionConfig) {
		ExecutorState state = this.monitor.getState();
		if (state.isStartable()) {
			this.monitor.setState(ExecutorState.CONNECTING, null);
			this.simple.execute(new ConnectionInitalize(sessionConfig, this, this.monitor));
		}
		else {
			throw new RuntimeException("Don't call initalize when state is: " + state);
		}
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

	public void setConnection(ConnectionHandler connection) {
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
	public ExecutorState getState() {
		return this.monitor.getState();
	}

	@Override
	public ConnectionHandlerStatus getConnectionStatus() {
		return this.monitor.getConnectionStatus();
	}

	@Override
	public void addListener(ExecutorStateListener listener) {
		this.monitor.addListener(listener);
	}

	@Override
	public void removeListener(ExecutorStateListener listener) {
		this.monitor.removeListener(listener);
	}

	@Override
	public void schedule(Runnable command, int repeatTimeInMillis) {
		this.executor.scheduleAtFixedRate(command, 0, repeatTimeInMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void schedule(Message message, int repeatTimeInMillis) {
		// TODO Auto-generated method stub
		
	}

}
