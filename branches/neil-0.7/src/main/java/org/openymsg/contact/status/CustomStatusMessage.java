package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

/**
 * Returns the custom status, or <tt>null</tt> if no such status has been set. Returns the custom status message, or
 * <tt>null</tt> if no such message has been set.
 */

public class CustomStatusMessage implements StatusMessage {
	/** An free form status message. */
	protected final String statusMessage;
	/** Either busy or available */
	protected final YahooStatus status;

	public CustomStatusMessage(YahooStatus status, String statusMessage) {
		this.status = status;
		this.statusMessage = statusMessage;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public YahooStatus getStatus() {
		return status;
	}

	@Override
	public String getStatusMessage() {
		return this.statusMessage;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof CustomStatusMessage)) return false;
		CustomStatusMessage other = (CustomStatusMessage) obj;
		if (statusMessage == null) {
			if (other.statusMessage != null) return false;
		} else if (!statusMessage.equals(other.statusMessage)) return false;
		if (status == null) {
			if (other.status != null) return false;
		} else if (!status.equals(other.status)) return false;
		return true;
	}

	@Override
	public boolean is(YahooStatus status) {
		return this.getStatus().equals(status);
	}

	@Override
	public String toString() {
		return "CustomStatusMessage [statusMessage=" + statusMessage + ", status=" + status + "]";
	}

}
