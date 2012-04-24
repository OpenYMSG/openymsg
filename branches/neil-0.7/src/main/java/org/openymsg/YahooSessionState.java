package org.openymsg;

public enum YahooSessionState {
	NOT_STARTED(true, false, false),
	STARTED(false, false, true),
	CONNECTED(false, false, true),
	LOGGED_IN(false, true, true),
	LOGGED_OUT(false, false, false),
	FAILURE(false, false, false);

	private final boolean readyToStart;
	private final boolean loggedIn;
	private final boolean available;

	private YahooSessionState(boolean readyToStart, boolean loggedIn, boolean available) {
		this.readyToStart = readyToStart;
		this.loggedIn = loggedIn;
		this.available = available;
	}

	public boolean isReadyToStart() {
		return readyToStart;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isAvailable() {
		return available;
	}
}
