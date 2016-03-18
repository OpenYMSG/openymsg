package org.openymsg.contact;

import org.openymsg.contact.group.SessionGroupCallback;
import org.openymsg.contact.roster.SessionRosterCallback;
import org.openymsg.contact.status.SessionStatusCallback;

public interface SessionContactCallback extends SessionRosterCallback, SessionGroupCallback, SessionStatusCallback {
}
