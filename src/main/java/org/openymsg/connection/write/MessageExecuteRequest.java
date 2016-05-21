package org.openymsg.connection.write;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.MessageRequest;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class MessageExecuteRequest implements MessageRequest {
	/** logger */
	private static final Log log = LogFactory.getLog(MessageExecuteRequest.class);
	private final Message message;
	private ConnectionHandler connection;

	public MessageExecuteRequest(Message message) {
		this.message = message;
	}

	@Override
	public void execute() {
		if (connection == null) {
			throw new IllegalStateException("Connection not set");
		}
		PacketBodyBuffer body = null;
		try {
			body = this.message.getBody();
			// TODO handle messages with extra
			// this.message.messageProcessed();
		} catch (IOException e) {
			// TODO handle exception
			e.printStackTrace();
			return;
		}
		ServiceType service = this.message.getServiceType();
		MessageStatus status = this.message.getMessageStatus();
		this.connection.sendPacket(body, service, status);
	}

	@Override
	public void failure(Exception ex) {
		log.error("Failed sending", ex);
	}

	public void setConnection(ConnectionHandler connection) {
		this.connection = connection;
	}

	@Override
	public String toString() {
		return String.format("MessageExecuteRequest [message=%s]", message);
	}
}
