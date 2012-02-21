package org.openymsg.execute.write;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.openymsg.execute.Message;
import org.openymsg.network.ConnectionHandler;

public class WaitForConnectionReader implements Runnable {
	private ConnectionHandler connection;
	private final ConcurrentLinkedQueue<Message> queue;
	private final ScheduledThreadPoolExecutor executor;

	public WaitForConnectionReader(ConcurrentLinkedQueue<Message> queue, ScheduledThreadPoolExecutor executor) {
		this.queue = queue;
		this.executor = executor;
	}

	@Override
	public void run() {
		try {
			if (connection == null) {
				return;
			}
			Message message = this.queue.peek();
			if (message != null) {
				DispatcherMessageRequest request = new DispatcherMessageRequest(message, connection);
				this.executor.execute(request);
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setConnection(ConnectionHandler connection) {
		this.connection = connection;
	}

}
