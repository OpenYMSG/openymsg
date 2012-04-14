package org.openymsg.contact.status;

public class ContactPresence {
	protected boolean onChat = false;
	protected boolean onPager = false;

	public ContactPresence(boolean onChat, boolean onPager) {
		this.onChat = onChat;
		this.onPager = onPager;
	}

	public boolean isOnline() {
		return this.onChat || this.onPager;
	}

}
