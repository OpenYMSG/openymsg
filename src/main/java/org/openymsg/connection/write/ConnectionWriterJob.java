package org.openymsg.connection.write;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.MessageRequest;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.execute.dispatch.ScheduleTaskCompletionException;
import org.openymsg.network.ConnectionHandler;

import java.util.Queue;

public class ConnectionWriterJob implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ConnectionWriterJob.class);
	private final ConnectionHandler connection;
	protected final Queue<MessageRequest> queue;
	protected boolean isFinished = false;

	public ConnectionWriterJob(Queue<MessageRequest> queue, ConnectionHandler connection) {
		this.queue = queue;
		this.connection = connection;
	}

	@Override
	public void execute() {
		if (this.isFinished) {
			throw new ScheduleTaskCompletionException();
		}
		if (isReadyToWrite()) {
			MessageRequest request = this.queue.poll();
			if (request != null) {
				request.setConnection(connection);
				try {
					request.execute();
				}
				// TODO - Throwable
				catch (Exception e) {
					if (e instanceof ScheduleTaskCompletionException) {
						throw (ScheduleTaskCompletionException) e;
					} else {
						request.failure(e);
					}
				}
			} else {
				log.trace("message is null");
			}
		}
	}

	protected boolean isReadyToWrite() {
		if (connection == null) {
			log.info("connection not set");
			return false;
		}
		return true;
	}

	public void finished() {
		this.isFinished = true;
	}

	@Override
	public void failure(Exception ex) {
		// TODO Handle failure,
		ex.printStackTrace();
	}

	// **TODO
	public boolean isLocked(int millisDuration) {
		return (this.connection != null) && this.connection.isLocked(millisDuration);
	}

	@Override
	public String toString() {
		return String.format("ConnectionWriterJob [queue size=%s, isFinished=%s]", queue.size(), isFinished);
	}
}
