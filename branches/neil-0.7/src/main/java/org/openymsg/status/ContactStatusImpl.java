package org.openymsg.status;

import org.openymsg.ContactStatus;
import org.openymsg.Status;

public class ContactStatusImpl implements ContactStatus {
    /**
     * The presence status of this user (away, available, etc).
     */
    protected Status status = Status.OFFLINE;

    protected int stealth; // Stealth status

    protected boolean onChat = false;
    protected boolean onPager = false;
    /**
     * Indicates that the user is on the ignore list (in other words: the local user does not want to receive messages
     * from this user).
     */
    protected boolean ignored = false;

    /**
     * Flag that determines if this user is on our StealthBlocked list (in other words, if this user is allowed to see
     * our details).
     */
    protected boolean stealthBlocked = false;

    /**
     * An free form status message.
     */
    protected String customStatusMessage;
    /**
     * A custom status. As yet I'm unsure if these are String identifiers, or numeric or even boolean values.
     */

    // TODO: Find out what values this can have (boolean, numeric, string?)
    protected String customStatus;

    /**
     * The amount of seconds that the user has been idle (or -1 if the idle time is unknown).
     */
    private long idleTime = -1;

    
    /**
     * Updates the YahooUser with the new values.
     * 
     * @param newStatus
     *            replacement for current Status
     * @param newVisibility
     *            replacement for current onChat and onPager values
     */
    public void update(Status newStatus, String newVisibility) {
        // This is the new version, where 13=combined pager/chat
        final int iVisibility = (newVisibility == null) ? 0 : Integer.parseInt(newVisibility);
        update(newStatus, (iVisibility & 2) > 0, (iVisibility & 1) > 0);
    }

    /**
     * Updates the YahooUser with the new values.
     * 
     * @param newStatus
     *            replacement for current Status value
     * @param newOnChat
     *            replacement for current onChat value
     * @param newOnPager
     *            replacement for current onPager value
     */
    public void update(Status newStatus, boolean newOnChat, boolean newOnPager) {
        // This is the old version, when 13=pager and 17=chat
        update(newStatus);

        this.onChat = newOnChat;
        this.onPager = newOnPager;
    }

    /**
     * Updates the YahooUser with the new values. This method should be called in cases when no chat or pager
     * information was provided (presumed that these values don't change in this case)
     * 
     * @param newStatus
     *            replacement for current Status value
     */
    public void update(Status newStatus) {
        // This is the old version, when 13=pager and 17=chat
        this.status = newStatus;

        if (this.status != Status.CUSTOM) {
            customStatusMessage = null;
            customStatus = null;
        }
    }
    
    /**
     * Returns the custom status, or <tt>null</tt> if no such status has been set.
     * 
     * @return The custom status or <tt>null</tt>.
     */
    public String getCustomStatus() {
        return customStatus;
    }

    /**
     * Sets the amount of seconds that this user has been idle.
     * <p>
     * Note that this method is used by internal library code, and should probably not be used by users of the OpenYMSG
     * library directly. *
     * 
     * @param seconds
     *            The idle time of this user
     */
    void setIdleTime(final long seconds) {
        idleTime = seconds;
    }

    /**
     * Returns the amount of seconds that this user has been idle, or -1 if this is unknown.
     * 
     * @return the amount of seconds that this user has been idle, or -1.
     */
    public long getIdleTime() {
        return idleTime;
    }

    /**
     * Sets a custom presence status and status message.
     * <p>
     * Note that setting these attributes does not update the custom status attributes of this User on the network. This
     * method is used by internal library code, and should probably not be used by users of the OpenYMSG library
     * directly.
     * 
     * @param message
     *            A free form text, describing the new status.
     * @param status
     *            A custom status.
     */
    public void setCustom(final String message, final String status) {
        customStatusMessage = message;
        customStatus = status;
    }

    /**
     * Returns the custom status message, or <tt>null</tt> if no such message has been set.
     * 
     * @return The custom status message or <tt>null</tt>.
     */
    public String getCustomStatusMessage() {
        return customStatusMessage;
    }

	@Override
	public String toString() {
		return "ContactStatusImpl [status=" + status + ", stealth=" + stealth + ", onChat=" + onChat + ", onPager="
				+ onPager + ", ignored=" + ignored + ", stealthBlocked=" + stealthBlocked + ", customStatusMessage="
				+ customStatusMessage + ", customStatus=" + customStatus + ", idleTime=" + idleTime + "]";
	}

}
