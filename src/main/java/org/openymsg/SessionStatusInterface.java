package org.openymsg;

public interface SessionStatusInterface {
	// SessionStatus getStatus();
	void checkStatusConnected() throws IllegalStateException;

	void checkStatusLoggingOut() throws IllegalStateException;

	void checkStatusLoggingIn() throws IllegalStateException;

}
