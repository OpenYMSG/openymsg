package org.openymsg.v1.network.event;

import java.util.Collections;
import java.util.Set;

import org.openymsg.network.ContactListType;
import org.openymsg.network.event.AbstractSessionListEvent;
import org.openymsg.network.event.DefaultSessionEvent;
import org.openymsg.network.event.SessionListEvent;
import org.openymsg.v1.network.YahooUserV1;

/**
 * Event that is triggered right after the session receives an entire list of
 * contacts from the server. This typically occurs right after authentication.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class SessionListEventV1 extends AbstractSessionListEvent<YahooUserV1> {

	public SessionListEventV1(Object source, ContactListType type, Set<YahooUserV1> contacts) {
		super(source, type, contacts);
	}
}
