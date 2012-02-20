package org.openymsg.execute.write;

import java.io.IOException;

import org.openymsg.execute.Message;
import org.openymsg.execute.Request;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class DispatcherMessageRequest implements Request {
	private Message message;
	private ConnectionHandler connection;
	
	public DispatcherMessageRequest(Message message, ConnectionHandler connection) {
		this.message = message;
		this.connection = connection;
	}

	@Override
	public void run() {
		try {
			PacketBodyBuffer body = null;
			try {
				body = this.message.getBody();
				this.message.messageProcessed();
			}
			catch (IOException e) {
				//TODO handle exception
				e.printStackTrace();
				return;
			}
			ServiceType service = this.message.getServiceType();
			MessageStatus status = this.message.getMessageStatus();
			this.connection.sendPacket(body, service, status);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
