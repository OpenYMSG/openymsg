package org.openymsg;

import org.openymsg.status.StatusMessage;

public interface ContactStatus {
	StatusMessage getStatus();

	Long getIdleTime();

}
