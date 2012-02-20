package org.openymsg.conference;

import java.util.Set;

import org.openymsg.Conference;

public class ConferenceImpl implements Conference {
	private String id;
	private Set<String> memberIds;

	// private String message;

	public ConferenceImpl(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Set<String> getMemberIds() {
		return this.memberIds;
	}

	// TODO - message is transient?
	// @Override
	// public String getMessage() {
	// return this.message;
	// }

}
