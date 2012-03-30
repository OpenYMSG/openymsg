package org.openymsg.session;

import java.io.IOException;

import org.openymsg.Status;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit the current status to the Yahoo network.
 */
// TODO - set status back
public class StatusChangeRequest implements Message {
	private Status status;
	private String customStatusMessage = null;

	public StatusChangeRequest(Status status) {
		this.status = status;
	}

	public StatusChangeRequest(Status status, String customStatusMessage) {
		this.status = status;
		this.customStatusMessage = customStatusMessage;
	}

	@Override
	public org.openymsg.network.PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("10", String.valueOf(status.getValue()));
		if (this.customStatusMessage == null) {
			body.addElement("19", "");
		}
		else {
			body.addElement("19", customStatusMessage);
			// TODO this is unicode I think
			body.addElement("97", "1");
		}
		return body;
	}

	@Override
	public org.openymsg.network.ServiceType getServiceType() {
		// TODO Auto-generated method stub
		return ServiceType.Y6_STATUS_UPDATE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

}
