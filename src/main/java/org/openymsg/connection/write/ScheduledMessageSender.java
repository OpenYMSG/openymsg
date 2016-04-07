package org.openymsg.connection.write;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Request;

/**
 * A request the sends a message when executed. This is primarily used for
 * scheduling messages to be sent in the future once or recurring.
 * 
 * @author neilhart
 */
public class ScheduledMessageSender implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ScheduledMessageSender.class);
	private PacketWriter executor;
	private Message message;

	public ScheduledMessageSender(PacketWriter executor, Message message) {
		if (executor == null) {
			throw new IllegalArgumentException("Executor cannot be null");
		}
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		this.executor = executor;
		this.message = message;
	}

	@Override
	public void execute() {
		this.executor.execute(this.message);
	}

	@Override
	// TODO need to do something
	public void failure(Exception ex) {
		log.warn("Exception processing the message: " + message, ex);
	}
}
