package org.openymsg.network.event;

import org.openymsg.network.YahooUser;

/**
 * A more specific SesionFriendEvent that gets thrown if a previous request to
 * become friends has been accepted.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public class SessionFriendAcceptedEvent extends SessionFriendEvent {

    private static final long serialVersionUID = 8585143061164760956L;

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
    public SessionFriendAcceptedEvent(Object source, YahooUser user,
            String message) {
        super(source, user);
        this.message = message;
    }
}
