package org.openymsg;

import org.openymsg.contact.status.StatusMessage;

public interface ContactStatus {
	StatusMessage getMessage();

	Long getIdleTime();

}
