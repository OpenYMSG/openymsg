package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

public interface StatusMessage {
	boolean isCustom();

	YahooStatus getStatus();

	String getStatusText();

	String getStatusMessage();

	boolean is(YahooStatus status);

	int hashCode();

	boolean equals(Object obj);

}
