package org.openymsg.contact.group;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

import java.io.IOException;

public class MoveContactToGroupMessage implements Message {
	private String username;
	private YahooContact contact;
	private YahooContactGroup from;
	private YahooContactGroup to;

	public MoveContactToGroupMessage(String username, YahooContact contact, YahooContactGroup from,
			YahooContactGroup to) {
		this.username = username;
		this.contact = contact;
		this.from = from;
		this.to = to;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("302", "240");
		body.addElement("300", "240");
		body.addElement("7", this.contact.getName());
		// TODO - handle protocol
		body.addElement("224", this.from.getName());
		body.addElement("264", this.to.getName());
		body.addElement("301", "240");
		body.addElement("303", "240");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.Y7_CHANGE_GROUP;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}
}
