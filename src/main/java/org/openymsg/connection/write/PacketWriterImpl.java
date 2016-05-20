package org.openymsg.connection.write;

import org.openymsg.execute.dispatch.MessageRequest;

import java.util.Queue;

public class PacketWriterImpl implements PacketWriter {
	private final Queue<MessageRequest> queue;

	public PacketWriterImpl(Queue<MessageRequest> queue) {
		this.queue = queue;
	}

	@Override
	public void execute(Message message) {
		MessageRequest request = wrapMessage(message);
		this.queue.add(request);
	}

	protected MessageRequest wrapMessage(Message message) {
		MessageRequest request = new MessageExecuteRequest(message);
		return request;
	}
}
