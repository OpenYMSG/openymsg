package org.openymsg.contact.status;

public class ContactPresence {
	public static final ContactPresence EMPTY = new ContactPresence(false, false);

	protected final boolean onChat;
	protected final boolean onPager;

	public ContactPresence(boolean onChat, boolean onPager) {
		this.onChat = onChat;
		this.onPager = onPager;
	}

	public boolean isOnline() {
		return this.onChat || this.onPager;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (onChat ? 1231 : 1237);
		result = prime * result + (onPager ? 1231 : 1237);
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ContactPresence)) return false;
		ContactPresence other = (ContactPresence) obj;
		if (onChat != other.onChat) return false;
		if (onPager != other.onPager) return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContactPresence [onChat=" + onChat + ", onPager=" + onPager + "]";
	}

}
