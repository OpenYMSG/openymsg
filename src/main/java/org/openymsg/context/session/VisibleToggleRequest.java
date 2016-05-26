package org.openymsg.context.session;

import java.io.IOException;

import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class VisibleToggleRequest implements Message {
	private boolean oldInvisibleStatus;
	private boolean newInvisibleStatus;

	public VisibleToggleRequest(boolean oldInvisibleStatus, boolean newInvisibleStatus) {
		this.oldInvisibleStatus = oldInvisibleStatus;
		this.newInvisibleStatus = newInvisibleStatus;
	}

	@Override
	public org.openymsg.network.PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();

		if (oldInvisibleStatus && !newInvisibleStatus) {
			body.addElement("13", "1");
		} else if (!oldInvisibleStatus && newInvisibleStatus) {
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
