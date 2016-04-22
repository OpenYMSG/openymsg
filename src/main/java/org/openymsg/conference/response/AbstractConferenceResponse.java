package org.openymsg.conference.response;

import org.openymsg.YahooConference;
import org.openymsg.conference.SessionConferenceCallback;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public abstract class AbstractConferenceResponse implements SinglePacketResponse {
	protected SessionConferenceCallback sessionConference;

	public AbstractConferenceResponse(SessionConferenceCallback sessionConference) {
		this.sessionConference = sessionConference;
	}

	protected YahooConference getConference(YMSG9Packet packet) {
		String id = packet.getValue("57");
		return new YahooConference(id);
	}
}