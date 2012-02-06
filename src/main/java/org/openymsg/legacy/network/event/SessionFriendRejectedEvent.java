package org.openymsg.legacy.network.event;

import org.openymsg.legacy.network.YahooUser;

/**
 * A more specific SesionFriendEvent that gets thrown if a previous request to become friends has been denied.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class SessionFriendRejectedEvent extends SessionFriendEvent {
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
    public SessionFriendRejectedEvent(Object source, YahooUser user, String message) {
        super(source, user);
        this.message = message;
    }
}
