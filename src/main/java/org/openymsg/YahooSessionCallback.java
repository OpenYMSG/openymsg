package org.openymsg;

import org.openymsg.auth.SessionAuthenticationCallback;
import org.openymsg.conference.SessionConferenceCallback;
import org.openymsg.connection.SessionConnectionCallback;
import org.openymsg.contact.SessionContactCallback;
import org.openymsg.message.SessionMessageCallback;
import org.openymsg.session.SessionSessionCallback;

public interface YahooSessionCallback extends SessionMessageCallback, SessionConnectionCallback,
		SessionAuthenticationCallback, SessionContactCallback, SessionConferenceCallback, SessionSessionCallback {

}
