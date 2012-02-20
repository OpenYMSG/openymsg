package org.openymsg.session;

import java.io.IOException;

import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a LOGOFF packet, which should exit us from Yahoo IM.
 */
public class LogoutMessage implements Message {
	private String username;

	public LogoutMessage(String username) {
		this.username = username;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", username);
		// TODO  Is this only in for HTTP?
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.LOGOFF;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	@Override
	public void messageProcessed() {
		// TODO Auto-generated method stub
		
	}

}
