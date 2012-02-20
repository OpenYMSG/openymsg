package org.openymsg.execute.write;

import org.openymsg.execute.Message;
import org.openymsg.execute.PacketWriter;
import org.openymsg.execute.Request;


public class WaitForConnectionRequest implements Request {
	private PacketWriter writer = null;
	private Message message;
	
	public WaitForConnectionRequest(Message message, PacketWriter writer) {
		this.message = message;
		this.writer = writer;
	}

	@Override
	public void run() {
		this.writer.execute(message);
	}

}
