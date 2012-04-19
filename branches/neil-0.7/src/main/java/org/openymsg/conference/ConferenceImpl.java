package org.openymsg.conference;

import org.openymsg.YahooConference;

/**
 * Internal representation of a Conference. Used to change state.
 * @author neilhart
 */
public class ConferenceImpl implements YahooConference {
	/** Unique id */
	private final String id;

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

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ConferenceImpl)) return false;
		ConferenceImpl other = (ConferenceImpl) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConferenceImpl [id=" + id + "]";
	}

}
