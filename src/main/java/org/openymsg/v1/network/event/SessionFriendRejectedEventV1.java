package org.openymsg.v1.network.event;

import org.openymsg.network.event.SessionFriendRejectedEvent;
import org.openymsg.v1.network.YahooUserV1;


/**
 * A more specific SesionFriendEvent that gets thrown if a previous request to
 * become friends has been denied.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class SessionFriendRejectedEventV1 extends SessionFriendEventV1 implements SessionFriendRejectedEvent<YahooUserV1> {
	private static final long serialVersionUID = 3415551157526861773L;

	/**
	 * Constructs new instance.
	 * 
	 * @param source
	 *            The logical source of this event.
	 * @param user
	 *            The contact that caused this event.
	 * @param message
	 *            An optional message, describing the event.s
	 */
	public SessionFriendRejectedEventV1(Object source, YahooUserV1 user,
			String message) {
		super(source, message, user);
	}
}
