package org.openymsg.network;

public enum MessageStatus {
	//TODO - match order 
    DEFAULT(0),
    SERVER_ACK(1),    
    TYPING(0x16),
    WEBLOGIN(0x5a55aa55),
    OFFLINE(0x5a55aa56);
    
    /** Unique long representation of this MessageStatus. */
    private long value;

    /**
     * Creates a new MessageStatus, based on a unique long value identifier.
     * 
     * @param value
     *            Unique long value for the MessageStatus to be created.
     */
    private MessageStatus(long value) {
        this.value = value;
    }

    /**
     * Gets the (unique) long value that identifies this MessageStatus.
     * 
     * @return long value identifying this MessageStatus.
     */
    public long getValue() {
        return value;
    }

    /**
     * Returns the Status that is identified by the provided long value. This method throws an IllegalArgumentException
     * if no matching MessageStatus is defined in this enumeration.
     * 
     * @param value
     *            MessageStatus identifier.
     * @return MessageStatus identified by 'value'.
     */
    public static MessageStatus getStatus(long value) {
        final MessageStatus[] all = MessageStatus.values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].getValue() == value) {
                return all[i];
            }
        }

        throw new IllegalArgumentException("No MessageStatus matching long value '" + value + "'.");
    }

}
