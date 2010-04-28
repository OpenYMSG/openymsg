package org.openymsg.network;

public enum YahooProtocol {
    /**
     * Different Yahoo messaging protocols
     */
    YAHOO(0), // really nothing
    LCS(1), MSN(2), LOTUS(9);

    // Unique long representation of this Status.
    private int value;

    /**
     * Creates a new YahooProtocol, based on a unique long value identifier.
     * 
     * @param value
     *            Unique long value for the YahooProtocol to be created.
     */
    private YahooProtocol(int value) {
        this.value = value;
    }

    /**
     * Gets the (unique) value that identifies this YahooProtocol.
     * 
     * @return value identifying this YahooProtocol.
     */
    public int getValue() {
        return value;
    }

    public String getStringValue() {
        return "" + value;
    }

    /**
     * Returns the YahooProtocol that is identified by the provided long value. This method throws an
     * IllegalArgumentException if no matching Status is defined in this enumeration.
     * 
     * @param value
     *            YahooProtocol identifier.
     * @return YahooProtocol identified by 'value'.
     */
    public static YahooProtocol getProtocol(int value) throws IllegalArgumentException {
        final YahooProtocol[] all = YahooProtocol.values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].getValue() == value) {
                return all[i];
            }
        }

        throw new IllegalArgumentException("No YahooProtocol matching long value '" + value + "'.");
    }

    public static YahooProtocol getProtocol(String protocol) throws IllegalArgumentException {
        if (protocol == null || protocol.trim().length() == 0) {
            return YahooProtocol.YAHOO;
        }
        int value = Integer.parseInt(protocol);
        return YahooProtocol.getProtocol(value);
    }

}
