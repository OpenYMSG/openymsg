package org.openymsg.context;

import org.openymsg.context.auth.SessionAuthenticationCallback;
import org.openymsg.context.session.SessionSessionCallback;

public interface SessionContextCallback extends SessionAuthenticationCallback, SessionSessionCallback {
}
