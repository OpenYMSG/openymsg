package org.openymsg.context.session;

public enum LogoutReason {
	NO_REASON(-1), // don't know, this is when no reason is sent, usually when normal logout
	DUPLICATE_LOGIN1(42), // 42 - Account has signed in from another location
	DUPLICATE_LOGIN2(99),
	UNKNOWN_52(52); // don't know

	private long value;

	/**
	 * Creates a new state, based on a unique long value identifier.
	 * @param value Unique long value for the state to be created.
	 */
	private LogoutReason(long value) {
		this.value = value;
	}

	/**
	 * Gets the (unique) long value that identifies this status.
	 * @return long value identifying this status.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Returns the AuthenticationState that is identified by the provided long value. This method throws an
	 * IllegalArgumentException if no matching AuthenticationState is defined in this enumeration.
	 * @param value AuthenticationState identifier.
	 * @return AuthenticationState identified by 'value'.
	 */
	public static LogoutReason getStatus(long value) {
		final LogoutReason[] all = LogoutReason.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getValue() == value) {
				return all[i];
			}
		}

		throw new IllegalArgumentException("No LogoutState matching long value '" + value + "'.");
	}

	public boolean isDuplicateLogin() {
		return this == DUPLICATE_LOGIN1 || this == DUPLICATE_LOGIN2;
	}

}
