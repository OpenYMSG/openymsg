package org.openymsg.execute.write;

import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.Request;

public class ScheduledMessageSender implements Request {
	public ScheduledMessageSender(Executor dispatcher, Message message) {
		this.dispatcher = dispatcher;
		this.message = message;
	}

	private Executor dispatcher;
	private Message message;

	@Override
	public void execute() {
		this.dispatcher.execute(this.message);
	}

	@Override
	public void failure(Exception ex) {
		// TODO Auto-generated method stub

	}

}
