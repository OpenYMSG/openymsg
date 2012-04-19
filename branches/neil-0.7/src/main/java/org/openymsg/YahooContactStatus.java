package org.openymsg;

import org.openymsg.contact.status.StatusMessage;

public interface YahooContactStatus {
	StatusMessage getMessage();

	Long getIdleTime();

}
