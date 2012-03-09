package org.openymsg.status;

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

}
