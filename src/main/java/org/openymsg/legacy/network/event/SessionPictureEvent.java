package org.openymsg.legacy.network.event;

/**
 * An SessionEvent that relates to incoming pictures (avatars).
 * @author Damian Minkov (damencho)
 */
public class SessionPictureEvent extends SessionEvent {
	protected byte[] data;

	/**
	 * Creates a new instance.
	 * @param source The source of the event.
	 * @param to The intended recipient.
	 * @param from The originator
	 * @param data the picture byte code.
	 */
	public SessionPictureEvent(Object source, String to, String from, byte[] data) {
		super(source, to, from, null);
		this.data = data;
	}

	/**
	 * Returns the picture byte code.
	 * @return The picture that was included in the event.
	 */
	public byte[] getPictureData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see org.openymsg.legacy.network.event.SessionEvent#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " data.len:" + data.length;
	}
}
