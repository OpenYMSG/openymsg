package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

/**
 * Returns the custom status, or <tt>null</tt> if no such status has been set. Returns the custom status message, or
 * <tt>null</tt> if no such message has been set.
 */

public class CustomStatusMessage implements StatusMessage {
	/** An free form status message. */
	protected final String statusMessage;
	/** A custom status. As yet I'm unsure if these are String identifiers, or numeric or even boolean values. */
	// TODO: Find out what values this can have (boolean, numeric, string?)
	protected final String statusText;

	public CustomStatusMessage(String statusText, String statusMessage) {
		this.statusText = statusText;
		this.statusMessage = statusMessage;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public YahooStatus getStatus() {
		return YahooStatus.CUSTOM;
	}

	@Override
	public String getStatusText() {
		return this.statusText;
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
		result = prime * result + ((statusText == null) ? 0 : statusText.hashCode());
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
		if (statusText == null) {
			if (other.statusText != null) return false;
		} else if (!statusText.equals(other.statusText)) return false;
		return true;
	}

	@Override
	public boolean is(YahooStatus status) {
		return this.getStatus().equals(status);
	}
}
