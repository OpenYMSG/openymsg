package org.openymsg.network.event;

import org.openymsg.network.YahooUser;


/**
 * A more specific SesionFriendEvent that gets thrown if a previous request to
 * become friends has been denied.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public interface SessionFriendRejectedEvent<T extends YahooUser> extends SessionFriendEvent<T> {

}
