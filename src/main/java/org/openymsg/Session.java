package org.openymsg;

import java.util.Set;

import org.openymsg.auth.SessionAuthentication;
import org.openymsg.conference.SessionConference;
import org.openymsg.connection.SessionConnection;
import org.openymsg.contact.group.SessionGroup;
import org.openymsg.contact.roster.SessionContact;
import org.openymsg.contact.status.SessionStatus;
import org.openymsg.message.SessionMessage;
import org.openymsg.session.SessionSession;

/**
 * Services for all Yahoo Messenger functionality.
 * @author neilhart
 */
public interface Session extends SessionConnection, SessionAuthentication, SessionSession, SessionMessage,
		SessionContact, SessionGroup, SessionStatus, SessionConference {

	/**
	 * @throws IllegalStateException if Session is not in correct state
	 */
	@Override
	void login(String username, String password) throws IllegalArgumentException, IllegalStateException;

	/**
	 * @throws IllegalStateException if Session is not in correct state
	 */
	@Override
	Set<ContactGroup> getContactGroups() throws IllegalStateException;

}
