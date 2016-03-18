package org.openymsg;

import org.openymsg.conference.SessionConference;
import org.openymsg.connection.SessionConnection;
import org.openymsg.contact.group.SessionGroup;
import org.openymsg.contact.roster.SessionRoster;
import org.openymsg.contact.status.SessionStatus;
import org.openymsg.context.SessionContext;
import org.openymsg.message.SessionMessage;

import java.util.Set;

/**
 * Services for all Yahoo Messenger functionality.
 * @author neilhart
 */
public interface YahooSession extends SessionConnection, SessionContext, SessionMessage, SessionRoster, SessionGroup,
		SessionStatus, SessionConference {
	/**
	 * @throws IllegalStateException if Session is not in correct state
	 */
	@Override
	void login(String username, String password) throws IllegalArgumentException, IllegalStateException;

	/**
	 * @throws IllegalStateException if Session is not in correct state
	 */
	@Override
	Set<YahooContactGroup> getContactGroups() throws IllegalStateException;

	boolean isShutdown();

	boolean isDisconnected();
}
