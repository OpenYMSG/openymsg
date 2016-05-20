package org.openymsg.connection.write;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Request;

/**
 * A request the sends a message when executed. This is primarily used for scheduling messages to be sent in the future
 * once or recurring.
 * @author neilhart
 */
public class ScheduledMessageSender implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(ScheduledMessageSender.class);
	private PacketWriter writer;
	private Message message;

	public ScheduledMessageSender(PacketWriter writer, Message message) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer cannot be null");
		}
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		this.writer = writer;
		this.message = message;
	}

	@Override
	public void execute() {
		this.writer.execute(this.message);
	}

	@Override
	// TODO need to do something
	public void failure(Exception ex) {
		log.warn("Exception processing the message: " + message, ex);
	}
}
