package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

public class PendingStatusMessage extends AbstractStatusMessage {
	public PendingStatusMessage() {
		super(YahooStatus.PENDING);
	}
}
