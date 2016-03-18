package org.openymsg;

import org.openymsg.conference.SessionConferenceCallback;
import org.openymsg.connection.SessionConnectionCallback;
import org.openymsg.contact.SessionContactCallback;
import org.openymsg.context.SessionContextCallback;
import org.openymsg.message.SessionMessageCallback;

public interface YahooSessionCallback extends SessionMessageCallback, SessionConnectionCallback, SessionContextCallback,
		SessionContactCallback, SessionConferenceCallback {
}
