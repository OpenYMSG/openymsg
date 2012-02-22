package org.openymsg;

import org.openymsg.auth.SessionAuthentication;
import org.openymsg.conference.SessionConference;
import org.openymsg.connection.SessionConnection;
import org.openymsg.contact.SessionContact;
import org.openymsg.group.SessionGroup;
import org.openymsg.message.SessionMessage;
import org.openymsg.session.SessionSession;
import org.openymsg.status.SessionStatus;

/**
 * Services for all Yahoo Messenger functionality.
 * 
 * @author neilhart
 */
public interface Session extends SessionConnection, SessionAuthentication, SessionSession, SessionMessage,
		SessionContact, SessionGroup, SessionStatus, SessionConference {

	/**
	 * @throws IllegalStateException if Session is not in correct state
	 */
	@Override
	void login(String username, String password) throws IllegalArgumentException, IllegalStateException;

}
