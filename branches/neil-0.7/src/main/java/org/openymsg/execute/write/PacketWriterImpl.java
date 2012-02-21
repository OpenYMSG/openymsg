package org.openymsg.execute.write;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openymsg.execute.Message;
import org.openymsg.execute.PacketWriter;
import org.openymsg.network.ConnectionHandler;

public class PacketWriterImpl implements PacketWriter {
	private ScheduledThreadPoolExecutor executor = null;
	private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
	private WaitForConnectionReader reader;

	public PacketWriterImpl(ScheduledThreadPoolExecutor executor) {
		this.executor = executor;
		this.reader = new WaitForConnectionReader(queue, this.executor);
		this.executor.scheduleAtFixedRate(this.reader, 0, 200, TimeUnit.MILLISECONDS);
	}
	
	public void setConnection(ConnectionHandler connection) {
		this.reader.setConnection(connection);
	}

	@Override
	public void execute(Message message) {
		this.queue.add(message);
	}

	@Override
	public void shutdown() {
	}
}
