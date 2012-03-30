package org.openymsg.contact.status;

import org.openymsg.Status;

public class NormalStatusMessage implements StatusMessage {
	private Status status;

	public NormalStatusMessage(Status status) {
		this.status = status;
	}

	@Override
	public boolean isCustom() {
		return false;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public String getStatusText() {
		return status.toString();
	}

	@Override
	public String getStatusMessage() {
		return null;
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NormalStatusMessage other = (NormalStatusMessage) obj;
		if (status != other.status) return false;
		return true;
	}

	@Override
	public String toString() {
		return "NormalStatusMessage [status=" + status + "]";
	}

	@Override
	public boolean is(Status status) {
		return this.getStatus().equals(status);
	}

}
