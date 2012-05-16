package org.openymsg;

public enum YahooSessionState {
	NOT_STARTED(),
	STARTED(),
	CONNECTED(),
	LOGGED_IN(),
	LOGGED_OUT(),
	FAILURE();

	private YahooSessionState() {
	}

	public boolean isReadyToStart() {
		return this == NOT_STARTED;
	}

	public boolean isLoggedIn() {
		return this == LOGGED_IN;
	}

	public boolean isAvailable() {
		return this == STARTED || this == CONNECTED || this == LOGGED_IN || this == LOGGED_OUT;
	}
}
