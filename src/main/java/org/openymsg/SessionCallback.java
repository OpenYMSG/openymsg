package org.openymsg;

import org.openymsg.auth.SessionAuthenticationCallback;
import org.openymsg.connection.SessionConnectionCallback;
import org.openymsg.message.SessionMessageCallback;

public interface SessionCallback extends SessionMessageCallback, SessionConnectionCallback, SessionAuthenticationCallback {

}
