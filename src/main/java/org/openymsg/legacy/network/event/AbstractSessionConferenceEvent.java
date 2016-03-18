package org.openymsg.legacy.network.event;

import org.openymsg.legacy.network.YahooConference;

public abstract class AbstractSessionConferenceEvent extends SessionEvent {
	private static final long serialVersionUID = 6875665694248382258L;
	protected YahooConference room;

	public AbstractSessionConferenceEvent(Object source) {
		super(source);
	}

	public AbstractSessionConferenceEvent(Object source, String to, String from, String message, YahooConference room) {
		super(source, to, from, message);
		this.room = room;
	}

	public YahooConference getRoom() {
		return room;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" room:").append(room);
		return sb.toString();
	}
}