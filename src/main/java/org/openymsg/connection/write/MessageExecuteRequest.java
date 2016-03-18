package org.openymsg.connection.write;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

import java.io.IOException;

public class MessageExecuteRequest implements Request {
	/** logger */
	private static final Log log = LogFactory.getLog(MessageExecuteRequest.class);
	private Message message;
	private ConnectionHandler connection;

	public MessageExecuteRequest(Message message, ConnectionHandler connection) {
		this.message = message;
		this.connection = connection;
	}

	@Override
	public void execute() {
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
}
