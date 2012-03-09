package org.openymsg.conference;

import org.openymsg.Conference;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public abstract class AbstractConferenceResponse implements SinglePacketResponse {
	protected SessionConferenceCallback sessionConference;

	public AbstractConferenceResponse(SessionConferenceCallback sessionConference) {
		this.sessionConference = sessionConference;
	}

	protected Conference getConference(YMSG9Packet packet) {
		String id = packet.getValue("57");
		return new ConferenceImpl(id);
	}
}