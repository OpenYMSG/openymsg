package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

public interface StatusMessage {
	boolean isCustom();

	YahooStatus getStatus();

	String getStatusMessage();

	boolean is(YahooStatus status);

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);
}
