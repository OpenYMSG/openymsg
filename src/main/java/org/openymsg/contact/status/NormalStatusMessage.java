package org.openymsg.contact.status;

import org.openymsg.YahooStatus;

public class NormalStatusMessage implements StatusMessage {
	private final YahooStatus status;

	public NormalStatusMessage(YahooStatus status) {
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
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof NormalStatusMessage)) return false;
		NormalStatusMessage other = (NormalStatusMessage) obj;
		if (status != other.status) return false;
		return true;
	}

	@Override
	public String toString() {
		return "NormalStatusMessage [status=" + status + "]";
	}

	@Override
	public boolean is(YahooStatus status) {
		return this.getStatus().equals(status);
	}

}
