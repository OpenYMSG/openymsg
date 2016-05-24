package org.openymsg.context.session;

import java.io.IOException;

import org.openymsg.YahooStatus;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class VisibleToggleRequest implements Message {
	private YahooStatus oldStatus;
	private YahooStatus newStatus;

	public VisibleToggleRequest(YahooStatus oldStatus, YahooStatus newStatus) {
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	@Override
	public org.openymsg.network.PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();

		if (oldStatus.isInvisible() && !newStatus.isInvisible()) {
			body.addElement("13", "1");
		} else if (!oldStatus.isInvisible() && newStatus.isInvisible()) {
			body.addElement("13", "2");
		}

		return body;
	}

	@Override
	public org.openymsg.network.ServiceType getServiceType() {
		return ServiceType.Y6_VISIBLE_TOGGLE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
