package org.openymsg.session;

public enum SessionState {
	LOGGING_IN(true, false, false),
	LOGGED_IN(false, true, false),
	LOGGING_OUT(false, false, true),
	LOGGED_OUT_NORMAL(false, false, false),
	LOGGED_OUT_FORCED(false, false, false);

	private final boolean loggingIn;
	private final boolean loggedIn;
	private final boolean loggingOff;

	private SessionState(boolean loggingIn, boolean loggedIn, boolean loggingOff) {
		this.loggingIn = loggingIn;
		this.loggedIn = loggedIn;
		this.loggingOff = loggingOff;
	}

	/**
	 * @return the loggingIn
	 */
	public boolean isLoggingIn() {
		return loggingIn;
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * @return the loggingOff
	 */
	public boolean isLoggingOff() {
		return loggingOff;
	}

}
