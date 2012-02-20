package org.openymsg.conference;

import java.io.IOException;
import java.util.Set;

import org.openymsg.Conference;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an CONFLOGOFF packet. We send one of these when we wish to leave a conference.
 */
public class LeaveConferenceMessage implements Message {
	public LeaveConferenceMessage(String username, Conference conference) {
		this.username = username;
		this.conference = conference;
	}

	private String username;
	private Conference conference;
	
	@Override
	public PacketBodyBuffer getBody() throws IOException {
        final Set<String> users = conference.getMemberIds();
        // Send decline packet to Yahoo
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", this.username);
        for (String user : users) {
            body.addElement("3", user);
        }
        body.addElement("57", conference.getId());
        return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.CONFLOGOFF;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.DEFAULT;
	}

	@Override
	public void messageProcessed() {
		//TODO - close conference
//      // Flag this conference as now dead
//      YahooConference yc = getConference(room);
//      yc.closeConference();
	}

}
