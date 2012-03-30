package org.openymsg.session;

import org.openymsg.Status;

public interface SessionSession {
	void logout();

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. If you want to login as invisible, set
	 * this to Status.INVISIBLE before you call login(). Note: this setter is overloaded. The second version is intended
	 * for use when setting custom status messages.
	 * @param status The new Status to be set for this user.
	 * @throws IllegalArgumentException
	 */
	void setStatus(Status status) throws IllegalArgumentException;

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc. Legit values are in the StatusConstants
	 * interface. If you want to login as invisible, set this to Status.INVISIBLE before you call login() Note: setter
	 * is overloaded, the second version is intended for use when setting custom status messages. The boolean is true if
	 * available and false if away.
	 * @param message
	 * @param showBusyIcon
	 * @throws IllegalArgumentException
	 */
	void setStatus(String message, boolean showBusyIcon) throws IllegalArgumentException;

}
