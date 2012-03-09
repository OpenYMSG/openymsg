package org.openymsg.status;

import org.openymsg.Status;

public interface StatusMessage {
	boolean isCustom();

	Status getStatus();

	String getStatusText();

	String getStatusMessage();
}
