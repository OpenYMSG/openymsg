package org.openymsg.contact.group;

import java.io.IOException;

import org.openymsg.YahooContactGroup;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a GOTGROUPRENAME packet, to change the name of one of our friends groups.
 */
/*
 * TODO: Currently, this behavior is as it was in jYMSG. Protocol specification would suggest that not 0x13
 * (GOTGROUPRENAME) but 0x89 (GROUPRENAME) should be used for this operation. Find out and make sure.
 */
public class ContactGroupRenameMessage implements Message {
	private final String username;
	private final YahooContactGroup group;
	private final String newGroupName;

	public ContactGroupRenameMessage(String username, YahooContactGroup group, String newGroupName) {
		this.username = username;
		this.group = group;
		this.newGroupName = newGroupName;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", this.username);
		body.addElement("65", this.group.getName());
		body.addElement("67", this.newGroupName);
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.GROUPRENAME;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	// @Override
	// public void messageProcessed() {
	// //TODO change group name
	// }

}
