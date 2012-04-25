package org.openymsg.conference;

import org.openymsg.YahooConference;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public abstract class AbstractConferenceResponse implements SinglePacketResponse {
	protected SessionConferenceImpl sessionConference;

	public AbstractConferenceResponse(SessionConferenceImpl sessionConference) {
		this.sessionConference = sessionConference;
	}

	protected YahooConference getConference(YMSG9Packet packet) {
		String id = packet.getValue("57");
		return new ConferenceImpl(id);
	}
}