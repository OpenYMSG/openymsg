package org.openymsg.contact.status;

import org.openymsg.YahooContactStatus;
import org.openymsg.YahooStatus;

//TODO handle stealthBlocked and stealth mode
public class ContactStatusImpl implements YahooContactStatus {
	/**
	 * The status message a user (away, available, etc).
	 */
	private StatusMessage status;

	/**
	 * The presene of a user
	 */
	private ContactPresence presence;

	/**
	 * The amount of seconds that the user has been idle (or -1 if the idle time is unknown).
	 */
	private Long idleTime = null;

	/**
	 * Updates the YahooUser with the new values.
	 * @param newStatus replacement for current Status
	 * @param newVisibility replacement for current onChat and onPager values
	 */
	public void update(YahooStatus newStatus, String newVisibility) {
		// This is the new version, where 13=combined pager/chat
		final int iVisibility = (newVisibility == null) ? 0 : Integer.parseInt(newVisibility);
		update(newStatus, (iVisibility & 2) > 0, (iVisibility & 1) > 0);
	}

	/**
	 * Updates the YahooUser with the new values.
	 * @param newStatus replacement for current Status value
	 * @param newOnChat replacement for current onChat value
	 * @param newOnPager replacement for current onPager value
	 */
	public void update(YahooStatus newStatus, boolean newOnChat, boolean newOnPager) {
		// This is the old version, when 13=pager and 17=chat
		update(newStatus);

		this.presence = new ContactPresence(newOnChat, newOnPager);
	}

	/**
	 * Updates the YahooUser with the new values. This method should be called in cases when no chat or pager
	 * information was provided (presumed that these values don't change in this case)
	 * @param newStatus replacement for current Status value
	 */
	public void update(YahooStatus newStatus) {
		// This is the old version, when 13=pager and 17=chat
		this.status = new NormalStatusMessage(newStatus);
	}

	/**
	 * Sets the amount of seconds that this user has been idle.
	 * <p>
	 * Note that this method is used by internal library code, and should probably not be used by users of the OpenYMSG
	 * library directly. *
	 * @param seconds The idle time of this user
	 */
	void setIdleTime(final long seconds) {
		if (seconds == -1) {
			idleTime = null;
		} else {
			idleTime = seconds;
		}
	}

	/**
	 * Returns the amount of seconds that this user has been idle, or -1 if this is unknown.
	 * @return the amount of seconds that this user has been idle, or -1.
	 */
	@Override
	public Long getIdleTime() {
		return idleTime;
	}

	/**
	 * Sets a custom presence status and status message.
	 * <p>
	 * Note that setting these attributes does not update the custom status attributes of this User on the network. This
	 * method is used by internal library code, and should probably not be used by users of the OpenYMSG library
	 * directly.
	 * @param message A free form text, describing the new status.
	 * @param status A custom status.
	 */
	public void setCustom(final String message, final String status) {
		this.status = new CustomStatusMessage(status, message);
	}

	@Override
	public String toString() {
		return "ContactStatusImpl [status=" + status + ", presence=" + presence + ", idleTime=" + idleTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idleTime == null) ? 0 : idleTime.hashCode());
		result = prime * result + ((presence == null) ? 0 : presence.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public StatusMessage getMessage() {
		return this.status;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ContactStatusImpl other = (ContactStatusImpl) obj;
		if (idleTime == null) {
			if (other.idleTime != null) return false;
		} else if (!idleTime.equals(other.idleTime)) return false;
		if (presence == null) {
			if (other.presence != null) return false;
		} else if (!presence.equals(other.presence)) return false;
		if (status == null) {
			if (other.status != null) return false;
		} else if (!status.equals(other.status)) return false;
		return true;
	}

}
