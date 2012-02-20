package org.openymsg;

import org.openymsg.auth.SessionAuthorize;
import org.openymsg.conference.SessionConference;
import org.openymsg.contact.SessionContact;
import org.openymsg.message.SessionMessage;
import org.openymsg.session.SessionSession;
import org.openymsg.status.SessionStatus;

public interface Session extends SessionAuthorize, SessionSession, SessionMessage, SessionContact, SessionStatus,
		SessionConference {

}
