package org.openymsg.contact.status;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;

public interface SessionStatusCallback {
	void statusUpdate(YahooContact contact, YahooContactStatus status);
}
