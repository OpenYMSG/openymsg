package org.openymsg.contact.status;

import org.openymsg.YahooContactStatus;
import org.openymsg.YahooStatus;

//TODO handle stealthBlocked and stealth mode
public class ContactStatusImpl implements YahooContactStatus {
	/** The status message a user (away, available, etc). */
	private final StatusMessage status;

	/** The presence of a user */
	private final ContactPresence presence;

	/** The amount of seconds that the user has been idle (or -1 if the idle time is unknown). */
	private final Long idleTime;

	public ContactStatusImpl(StatusMessage status, ContactPresence presence, Long idleTime) {
		this.status = status;
		this.presence = presence;
		this.idleTime = idleTime;
	}

	@Deprecated
	public ContactStatusImpl(YahooStatus status, boolean onChat, boolean onPager) {
		if (status.isCustom()) {
			throw new IllegalArgumentException("status cannot be custom");
		}
		this.status = new NormalStatusMessage(status);
		this.presence = new ContactPresence(onChat, onPager);
		this.idleTime = -1L;
	}

	/**
	 * Returns the amount of seconds that this user has been idle, or -1 if this is unknown.
	 * @return the amount of seconds that this user has been idle, or -1.
	 */
	@Override
	public Long getIdleTime() {
		return idleTime;
	}

	@Override
	public StatusMessage getMessage() {
		return this.status;
	}

	@Override
	public String toString() {
		return "ContactStatusImpl [status=" + status + ", presence=" + presence + ", idleTime=" + idleTime + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idleTime == null) ? 0 : idleTime.hashCode());
		result = prime * result + ((presence == null) ? 0 : presence.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ContactStatusImpl)) return false;
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
