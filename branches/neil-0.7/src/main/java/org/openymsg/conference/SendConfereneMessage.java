package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.Conference;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFMSG packet. We send one of these when we wish to send a message to a conference.
 */
public class SendConfereneMessage implements Message {
	private String username;
	private Conference conference;
	private String message;

	public SendConfereneMessage(String username, Conference conference, String message) {
		this.username = username;
		this.conference = conference;
		this.message = message;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", username);
        for (String member : this.conference.getMemberIds()) {
            body.addElement("53", member);
        }
        body.addElement("57", this.conference.getId());
        body.addElement("14", message);
        //TODO - where to put this?
//        if (Util.isUtf8(message)) body.addElement("97", "1");
        return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFMSG;
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