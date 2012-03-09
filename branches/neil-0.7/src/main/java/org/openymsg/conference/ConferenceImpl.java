package org.openymsg.conference;

import org.openymsg.Conference;

/**
 * Internal representation of a Conference. Used to change state.
 * @author neilhart
 */
public class ConferenceImpl implements Conference {
	/** Unique id */
	private String id;

	/**
	 * Create a Conference with the unique id
	 * @param id unique id
	 */
	public ConferenceImpl(String id) {
		this.id = id;
	}

	/**
	 * Get the unique id
	 */
	@Override
	public String getId() {
		return this.id;
	}

}
