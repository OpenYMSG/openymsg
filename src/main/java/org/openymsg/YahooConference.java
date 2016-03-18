package org.openymsg;

/**
 * Conference information. A conference has a unique id.
 * @author neilhart
 */
public class YahooConference {
	/** Unique id */
	private final String id;

	/**
	 * Create a Conference with the unique id
	 * @param id unique id
	 */
	public YahooConference(String id) {
		this.id = id;
	}

	/**
	 * Get the unique id
	 */
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof YahooConference))
			return false;
		YahooConference other = (YahooConference) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "YahooConference [id=" + id + "]";
	}
}
