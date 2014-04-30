package org.openymsg.context.session;

import java.io.IOException;

import org.openymsg.YahooStatus;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit the current status to the Yahoo network.
 */
// TODO - set status back
public class StatusChangeRequest implements Message {
	private YahooStatus status;
	private String customStatusMessage = null;
	private boolean showBusy = false;

	public StatusChangeRequest(YahooStatus status) {
		this.status = status;
	}

	public StatusChangeRequest(YahooStatus status, String customStatusMessage, boolean showBusy) {
		this.status = status;
		this.customStatusMessage = customStatusMessage;
		this.showBusy = showBusy;
	}

	@Override
	public org.openymsg.network.PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("10", String.valueOf(status.getValue()));
		if (this.customStatusMessage == null) {
			body.addElement("19", "");
		} else {
			body.addElement("19", customStatusMessage);
			// TODO this is unicode I think
			body.addElement("97", "1");
		}
		if (showBusy) {
			body.addElement("47", "1");
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
