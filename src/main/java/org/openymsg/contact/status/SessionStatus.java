package org.openymsg.contact.status;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;

public interface SessionStatus {

	YahooContactStatus getStatus(YahooContact contact);
}
