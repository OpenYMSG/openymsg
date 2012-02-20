package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.Conference;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFLOGON packet. Send this when we want to accept an offer to join a conference.
 */
public class AcceptConferenceMessage implements Message {
	private String username;
	private Conference conference;
	
	public AcceptConferenceMessage(String username, Conference conference) {
		this.username = username;
		this.conference = conference;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", username);
        for (String user : this.conference.getMemberIds())
            body.addElement("3", user);
        body.addElement("57", this.conference.getId());
        return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFLOGON;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	@Override
	public void messageProcessed() {
		//TODO - join conference?
	}

}
