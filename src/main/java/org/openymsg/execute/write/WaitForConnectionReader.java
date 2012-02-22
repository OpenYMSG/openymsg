package org.openymsg.execute.write;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.Message;
import org.openymsg.network.ConnectionHandler;

public class WaitForConnectionReader implements Runnable {
	private static final Log log = LogFactory.getLog(WaitForConnectionReader.class);
	private ConnectionHandler connection;
	private ConcurrentLinkedQueue<Message> queue;
	private ScheduledThreadPoolExecutor executor;

	public WaitForConnectionReader(ConcurrentLinkedQueue<Message> queue, ScheduledThreadPoolExecutor executor) {
		this.queue = queue;
		this.executor = executor;
	}

	@Override
	public void run() {
		try {
			if (connection == null) {
				log.info("connection not set");
				return;
			}
			Message message = this.queue.poll();
			if (message != null) {
				DispatcherMessageRequest request = new DispatcherMessageRequest(message, connection);
				this.executor.execute(request);
			}
			else {
				log.info("message is null");
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
