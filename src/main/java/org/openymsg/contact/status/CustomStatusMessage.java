package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

/**
 * Returns the custom status, or <tt>null</tt> if no such status has been set. Returns the custom status message, or
 * <tt>null</tt> if no such message has been set.
 */
public class CustomStatusMessage extends AbstractStatusMessage {
	/** An free form status message. */
	protected final String statusMessage;

	public CustomStatusMessage(YahooStatus status, String statusMessage) {
		super(status);
		this.statusMessage = statusMessage;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public String getStatusMessage() {
		return this.statusMessage;
	}

	@Override
	public String toString() {
		return "CustomStatusMessage [statusMessage=" + statusMessage + ", status=" + this.getStatus() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomStatusMessage other = (CustomStatusMessage) obj;
		if (statusMessage == null) {
			if (other.statusMessage != null)
				return false;
		} else if (!statusMessage.equals(other.statusMessage))
			return false;
		return true;
	}
}
