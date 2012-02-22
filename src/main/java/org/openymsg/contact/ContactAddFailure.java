package org.openymsg.contact;


public enum ContactAddFailure {
	UNKNOWN(-1),
	ALREADY_IN_GROUP(2), // TODO - may be the big list or the group
	NOT_YAHOO_USER(3),
	SOMETHING(33),
	NOT_REMOTE_USER(40), //for other protocols
	SOMETHING_ELSE(42);

	private int value;

	/**
	 * Creates a new ContactAddFailure, based on a unique long value identifier.
	 * 
	 * @param value Unique long value for the Status to be created.
	 */
	private ContactAddFailure(int value) {
		this.value = value;
	}

	/**
	 * Gets the (unique) long value that identifies this ContactAddFailure.
	 * 
	 * @return long value identifying this ContactAddFailure.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Returns the Status that is identified by the provided long value. This method throws an IllegalArgumentException
	 * if no matching Status is defined in this enumeration.
	 * 
	 * @param value ContactAddFailure identifier.
	 * @return ContactAddFailure identified by 'value'.
	 */
	public static ContactAddFailure get(long value) {
		final ContactAddFailure[] all = ContactAddFailure.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getValue() == value) {
				return all[i];
			}
		}

		throw new IllegalArgumentException("No ContactAddFailure matching long value '" + value + "'.");
	}

	public static ContactAddFailure get(String value) throws IllegalArgumentException {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        long longValue;
		try {
			longValue = Integer.parseInt(value);
	        return get(longValue);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("No ContactAddFailure matching string value '" + value + "'.");
		}
	}
}
