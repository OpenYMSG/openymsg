package org.openymsg.context.session;

import org.openymsg.YahooStatus;

public interface SessionSession {
	/**
	 * Logout of session
	 */
	void logout();

	/**
	 * Keep alive called to prevent connection shutting down.
	 */
	void keepAlive();

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. If you want to login as invisible, set
	 * this to Status.INVISIBLE before you call login(). Note: this setter is overloaded. The second version is intended
	 * for use when setting custom status messages.
	 * @param status The new Status to be set for this user.
	 * @throws IllegalArgumentException
	 */
	void setStatus(YahooStatus status) throws IllegalArgumentException;

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. Legit values are in the StatusConstants
	 * interface. If you want to login as invisible, set this to Status.INVISIBLE before you call login() Note: setter
	 * is overloaded, the second version is intended for use when setting custom status messages. The boolean is true if
	 * available and false if away.
	 * @param message
	 * @param showBusyIcon
	 * @throws IllegalArgumentException
	 */
	void setCustomStatus(String message, boolean showBusyIcon) throws IllegalArgumentException;
}
