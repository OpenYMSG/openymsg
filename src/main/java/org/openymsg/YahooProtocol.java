package org.openymsg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Different Yahoo messaging protocols
 */
public enum YahooProtocol {
	YAHOO("0"), // really nothing
	LCS("1"),
	MSN("2"),
	LOTUS("9");

	/** logger */
	private static final Log log = LogFactory.getLog(YahooProtocol.class);
	/** Unique long representation of this Status */
	private String value;

	/**
	 * Creates a new YahooProtocol, based on a unique long value identifier.
	 * @param value Unique long value for the YahooProtocol to be created.
	 */
	private YahooProtocol(String value) {
		this.value = value;
	}

	/**
	 * Gets the (unique) value that identifies this YahooProtocol.
	 * @return value identifying this YahooProtocol.
	 */
	public String getValue() {
		return value;
	}

	public String getStringValue() {
		return "" + value;
	}

	/**
	 * Returns the YahooProtocol that is identified by the provided long value. This method throws an
	 * IllegalArgumentException if no matching Status is defined in this enumeration.
	 * @param value YahooProtocol identifier.
	 * @return YahooProtocol identified by 'value'.
	 */
	public static YahooProtocol getProtocol(String value) throws IllegalArgumentException {
		final YahooProtocol[] all = YahooProtocol.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getValue().equals(value)) {
				return all[i];
			}
		}

		throw new IllegalArgumentException("No YahooProtocol matching long value '" + value + "'.");
	}

	// public static YahooProtocol getProtocol(String protocol) throws IllegalArgumentException {
	// if (protocol == null || protocol.trim().length() == 0) {
	// return YahooProtocol.YAHOO;
	// }
	// int value;
	// try {
	// value = Integer.parseInt(protocol);
	// }
	// catch (Exception e) {
	// throw new IllegalArgumentException("No YahooProtocol matching string value '" + protocol + "'.");
	// }
	// return YahooProtocol.getProtocol(value);
	// }

	public boolean isYahoo() {
		return this.equals(YAHOO);
	}

	public static YahooProtocol getProtocolOrDefault(String protocolString, String who) {
		try {
			return YahooProtocol.getProtocol(protocolString);
		}
		catch (IllegalArgumentException e) {
			log.error("Failed finding protocol: " + protocolString + " for user: " + who);
			return YahooProtocol.YAHOO;
		}
	}

	public boolean isMsn() {
		return this.equals(MSN);
	}

}
