package org.openymsg.contact.status;

import org.openymsg.Status;

public interface StatusMessage {
	boolean isCustom();

	Status getStatus();

	String getStatusText();

	String getStatusMessage();
}
