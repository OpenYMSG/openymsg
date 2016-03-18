package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

public abstract class AbstractStatusMessage implements StatusMessage {
	/** Either busy or available */
	private final YahooStatus status;

	public AbstractStatusMessage(YahooStatus status) {
		this.status = status;
	}

	@Override
	public boolean isCustom() {
		return false;
	}

	@Override
	public YahooStatus getStatus() {
		return this.status;
	}

	@Override
	public String getStatusMessage() {
		return null;
	}

	@Override
	public boolean is(YahooStatus status) {
		return this.getStatus().equals(status);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractStatusMessage))
			return false;
		AbstractStatusMessage other = (AbstractStatusMessage) obj;
		if (status != other.status)
			return false;
		return true;
	}
}
