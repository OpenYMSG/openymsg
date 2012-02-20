package org.openymsg.message;

import org.openymsg.execute.read.SinglePacketResponseAbstract;

public class MessageOnlineResponse extends SinglePacketResponseAbstract {
	private final static String BUZZ = "<ding>";
	private SessionMessageImpl session;

	public MessageOnlineResponse(SessionMessageImpl session) {
		this.session = session;
	}

	@Override
	protected void execute() {
		// Sent while we are online
		// TODO - handle indentity
		// TODO - handle MSN
		// final String to = this.packet.getValue("5");
		String from = this.packet.getValue("4");
		String message = this.packet.getValue("14");
		String id = this.packet.getValue("429");
		this.session.receivedMessage(from, message, id);

		// TODO - handle message
		// final SessionEvent se = new SessionEvent(this, to, from, message);
		if (message.equalsIgnoreCase(BUZZ)) {
			this.session.receivedBuzz(from, id);
		}
		else {
			this.session.receivedMessage(from, message, id);
		}
	}

}
